package com.vs.video_stream.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vs.video_stream.enums.FileType;
import com.vs.video_stream.enums.Status;
import com.vs.video_stream.exception.NotValidMedia;
import com.vs.video_stream.model.MediaMetaData;
import com.vs.video_stream.rabbitmq.Publisher;
import com.vs.video_stream.repository.primary.MediaMetaDataRepository;
import com.vs.video_stream.utils.Constant;
import com.vs.video_stream.utils.FileTypeChecker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

  @Value("${app.base.directory}")
  private String baseDir;

  @Value("${app.rabbitmq.trove.trove-routing-key2}")
  private String troveKMRoutingKey;

  @Value("${ffmpeg.command.audio}")
  private String audioCommand;
  @Value("${ffmpeg.command.video-has-audio}")
  private String videoHasAudioCommand;
  @Value("${ffmpeg.command.video-with-audio}")
  private String videoWithAudioCommand;
  @Value("${ffmpeg.command.video-without-audio}")
  private String videoWithoutAudioCommand;
  @Value("${ffmpeg.command.preview}")
  private String previewCommand;
  @Value("${ffmpeg.command.thumbnail}")
  private String thumbnailCommand;
  @Value("${ffmpeg.command.image.gif}")
  private String imageGifcommand;
  @Value("${ffmpeg.command.image.generic}")
  private String imageGenericcommand;

  private final FileTypeChecker fileTypeChecker;
  private final AzureBlobService azureBlobService;
  private final MediaMetaDataRepository mediaMetaDataRepository;
  private final Publisher publisher;

  public MediaMetaData processMedia(MediaMetaData metaData) {

    metaData.setStatus(Status.IN_PROGRESS);
    mediaMetaDataRepository.save(metaData);
    publisher.publish(metaData, troveKMRoutingKey);

    long startTime = System.nanoTime();
    try {

      String uniqueId = UUID.randomUUID().toString();
      File tempFile = azureBlobService.downloadBlobToTempFile(metaData.getOriginalFileName(), uniqueId);

      metaData = this.processAndUploadMedia(
          tempFile,
          uniqueId,
          metaData);
      metaData.setStatus(Status.COMPLETED);
    } catch (NotValidMedia e) {

      metaData.setStatus(Status.NOT_REQUIRED);
      metaData.setErrorMessage(e.getMessage());
      log.error("File Name : {}, error : ", metaData.getOriginalFileName(), e.getMessage());
    } catch (Exception e) {

      metaData.setStatus(Status.FAILED);
      metaData.setErrorMessage(e.getMessage());
      log.error("File Name : {}, error : ", metaData.getOriginalFileName(), e.getMessage());
    }
    long endTime = System.nanoTime();
    long processingTime = (endTime - startTime) / 1_000_000;
    metaData.setProcessingTime(processingTime);
    mediaMetaDataRepository.save(metaData);
    publisher.publish(metaData, troveKMRoutingKey);

    System.out.println("metaData : " + metaData);
    return metaData;

  }

  private MediaMetaData processAndUploadMedia(
      File inputFile,
      String uniqueId,
      MediaMetaData metaData) throws IOException, NotValidMedia, InterruptedException {

    String inputFileName = inputFile.getName();
    String inputFilePath = inputFile.getAbsolutePath();
    String folderName = "processed_" + uniqueId;
    String outputDirPath = baseDir + File.separator + folderName;

    File outputDirectory = new File(outputDirPath);
    if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
      throw new IOException("Failed to create output directory: " + outputDirPath);
    }
    metaData.setFolderName(folderName);
    metaData.setProcessedFileName(Constant.MASTER_PLAYLIST_NAME);

    String ffmpegCommand = determineFFmpegCommand(
        inputFile,
        inputFileName,
        inputFilePath,
        outputDirPath,
        Constant.MASTER_PLAYLIST_NAME,
        metaData);

    executeFFmpegCommand(ffmpegCommand);
    uploadProcessedFiles(outputDirectory, folderName);
    cleanupFiles(inputFile, outputDirectory);

    return metaData;
  }

  private String determineFFmpegCommand(
      File file,
      String inputFileName,
      String inputFilePath,
      String outputDirPath,
      String masterPlaylistName,
      MediaMetaData metaData) throws NotValidMedia, IOException, InterruptedException {

    long fileSizeInBytes = file.length();
    long fileSizeInMB = fileSizeInBytes / (1024 * 1024);

    if (fileTypeChecker.isImageFile(inputFileName)) {
      if (fileSizeInMB < 1) {
        throw new NotValidMedia("Image file size is less than 3 MB. Skipping processing.");
      }
      String compressedFileName = "compressed_" + inputFileName;
      String compressedFilePath = outputDirPath + File.separator + compressedFileName;
      metaData.setFileType(FileType.IMAGE);
      metaData.setProcessedFileName(compressedFileName);
      String command;
      if (inputFileName.toLowerCase().endsWith(".gif")) {
        command = String.format(imageGifcommand, inputFilePath, compressedFilePath);
      } else {
        command = String.format(imageGenericcommand, inputFilePath, compressedFilePath);
      }
      return command;
    }
    if (fileTypeChecker.isAudioFile(inputFileName)) {
      if (fileSizeInMB < 3) {
        throw new NotValidMedia("Audio file size is less than 3 MB. Skipping processing.");
      }
      metaData.setFileType(FileType.AUDIO);
      return String.format(audioCommand, inputFilePath, outputDirPath, masterPlaylistName, outputDirPath);
    }
    if (fileTypeChecker.isVideoFile(inputFileName)) {
      if (fileSizeInMB < 10) {
        throw new NotValidMedia("Video file size is less than 10 MB. Skipping processing.");
      }
      metaData.setFileType(FileType.VIDEO);
      metaData.setThumbnailFileName(Constant.THUMBNAIL_FILENAME);
      metaData.setPreviewFileName(Constant.PREVIEW_FILENAME);
      generateVideoPreview(inputFilePath, outputDirPath);
      generateThumbnail(inputFilePath, outputDirPath);
      String command = videoHasAudio(inputFilePath) ? videoWithAudioCommand : videoWithoutAudioCommand;
      return String.format(command, inputFilePath, outputDirPath, masterPlaylistName, outputDirPath);
    }
    throw new NotValidMedia("Unknown file type or file does not meet size requirements.");
  }

  private void executeFFmpegCommand(String ffmpegCommand) throws IOException, InterruptedException {

    ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand.split(" "));
    processBuilder.redirectErrorStream(true);
    File logFile = new File("ffmpeg_error.log");
    processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
    Process process = null;
    try {
      process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException("FFmpeg processing failed with exit code " + exitCode);
      }
    } finally {
      if (process != null) {
        process.destroy();
      }
    }
  }

  private void generateVideoPreview(
      String inputFilePath,
      String outputDirPath) throws IOException, InterruptedException {

    String previewFilePath = outputDirPath + File.separator + Constant.PREVIEW_FILENAME;
    String command = String.format(previewCommand, inputFilePath, previewFilePath);
    executeFFmpegCommand(command);
  }

  private void generateThumbnail(
      String inputFilePath,
      String outputDirPath) throws IOException, InterruptedException {

    String thumbnailFilePath = outputDirPath + File.separator + Constant.THUMBNAIL_FILENAME;
    String command = String.format(thumbnailCommand, inputFilePath, thumbnailFilePath);
    executeFFmpegCommand(command);
  }

  private void uploadProcessedFiles(
      File outputDirectory,
      String folderName) {

    try {
      for (File fileToUpload : outputDirectory.listFiles()) {
        if (fileToUpload.isFile()) {
          azureBlobService.uploadFile(fileToUpload, folderName);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error while uploading files to Azure Blob Storage.", e);
    }
  }

  private boolean videoHasAudio(String filePath) {

    String command = String.format(videoHasAudioCommand, filePath);
    try {
      Process process = new ProcessBuilder(command.split(" ")).start();
      int exitCode = process.waitFor();
      return exitCode == 0 && process.getInputStream().read() != -1;
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Error while checking for audio streams.", e);
    }
  }

  private void cleanupFiles(
      File inputFile,
      File outputDirectory) {

    deleteFile(inputFile);
    deleteDirectory(outputDirectory);
  }

  private void deleteFile(File file) {

    if (file.exists() && !file.delete()) {
      System.err.println("Warning: Failed to delete file: " + file.getAbsolutePath());
    }
  }

  private void deleteDirectory(File directory) {

    if (directory.exists()) {
      for (File file : directory.listFiles()) {
        deleteFile(file);
      }
      if (!directory.delete()) {
        System.err.println("Warning: Failed to delete directory: " + directory.getAbsolutePath());
      }
    }
  }
}
