package com.vs.video_stream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureBlobConfig {

  @Value("${azure.storage.connection-string}")
  private String connectionString;
  @Value("${azure.storage.container-name}")
  private String containerName;
  @Value("${azure.storage.sas-token-duration}")
  private int sasTokenDuration;

  @Bean
  BlobContainerClient blobServiceClient() {
    return new BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient()
        .getBlobContainerClient(containerName);
  }

}