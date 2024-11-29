package com.vs.video_stream.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileTypeChecker {

  @Value("${file-types.audio-extensions}")
  private List<String> audioExtensions;
  @Value("${file-types.image-extensions}")
  private List<String> imageExtensions;
  @Value("${file-types.video-extensions}")
  private List<String> videoExtensions;
  @Value("${file-types.document-extensions}")
  private List<String> documentExtensions;

  public boolean isAudioFile(String fileName) {
    return hasExtension(fileName, audioExtensions);
  }

  public boolean isImageFile(String fileName) {
    return hasExtension(fileName, imageExtensions);
  }

  public boolean isVideoFile(String fileName) {
    return hasExtension(fileName, videoExtensions);
  }

  public boolean isDocumentFile(String fileName) {
    return hasExtension(fileName, documentExtensions);
  }

  private boolean hasExtension(String fileName, List<String> extensions) {
    String extension = getFileExtension(fileName);
    return extensions.contains(extension.toLowerCase());
  }

  private String getFileExtension(String fileName) {
    if (fileName != null && fileName.contains(".")) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    return "";
  }

}
