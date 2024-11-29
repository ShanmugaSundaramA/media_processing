package com.vs.video_stream.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vs.video_stream.model.MediaMetaData;
import com.vs.video_stream.rabbitmq.Publisher;
import com.vs.video_stream.service.AzureBlobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

  @Value("${app.rabbitmq.trove.trove-routing-key1}")
  private String troveVideoProcessingRoutingKey;
  private final AzureBlobService azureBlobService;
  private final Publisher publisher;

  @PostMapping("/upload")
  public ResponseEntity<Object> uploadFile(@RequestParam MultipartFile file) throws IOException {
    return new ResponseEntity<>(azureBlobService.uploadFile(file), HttpStatus.OK);
  }

  @PostMapping("/sasToken")
  public ResponseEntity<Object> sasToken() {
    return new ResponseEntity<>(azureBlobService.generateContainerSasToken(), HttpStatus.OK);
  }

  @PostMapping("/process")
  public ResponseEntity<Object> mediaProcess(@RequestBody MediaMetaData mediaMetaData) {
    return new ResponseEntity<>(publisher.publish(mediaMetaData, troveVideoProcessingRoutingKey), HttpStatus.OK);
  }

}
