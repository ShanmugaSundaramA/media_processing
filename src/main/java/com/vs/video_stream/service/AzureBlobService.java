package com.vs.video_stream.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AzureBlobService {

  @Value("${app.base.directory}")
  private String baseDir;
  @Value("${azure.storage.sasTokenDuration}")
  private int sasTokenDuration;

  private final BlobContainerClient blobContainerClient;

  public String generateContainerSasToken() {
    BlobContainerSasPermission permissions = new BlobContainerSasPermission()
        .setReadPermission(true)
        .setListPermission(true);
    OffsetDateTime expiryTime = OffsetDateTime
        .now(ZoneOffset.UTC)
        .plusSeconds(sasTokenDuration);
    BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(
        expiryTime,
        permissions).setProtocol(SasProtocol.HTTPS_HTTP);
    return blobContainerClient.generateSas(sasValues);
  }

  public String getFileUrl(String fileName) {
    return blobContainerClient.getBlobClient(fileName).getBlobUrl() + "?" + generateContainerSasToken();
  }

  public String uploadFile(MultipartFile file) throws IOException {

    BlobClient blobClient = blobContainerClient.getBlobClient(file.getOriginalFilename());
    blobClient.upload(file.getInputStream(), file.getSize(), true);
    return blobClient.getBlobUrl();
  }

  public void uploadFile(
      File file,
      String folderName) {

    String blobPath = folderName + "/" + file.getName();
    blobContainerClient.getBlobClient(blobPath).uploadFromFile(file.getAbsolutePath(), true);
  }

  public File downloadBlobToTempFile(
      String fileName,
      String uniqueId) throws IOException {

    File customDir = new File(baseDir);
    if (!customDir.exists() && !customDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + baseDir);
    }
    File customFile = new File(customDir, uniqueId + "_" + fileName);
    BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
    if (!blobClient.exists()) {
      throw new IOException("Blob does not exist: " + fileName);
    }
    blobClient.downloadToFile(customFile.getAbsolutePath());
    return customFile;
  }

}
