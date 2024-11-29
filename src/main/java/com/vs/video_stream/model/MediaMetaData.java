package com.vs.video_stream.model;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vs.video_stream.enums.FileType;
import com.vs.video_stream.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "mediaMetaData")
public class MediaMetaData {

  @Id
  private String id;
  private Status status;
  private FileType fileType;
  private String folderName;
  private String originalFileName;
  private String processedFileName;
  private String previewFileName;
  private String thumbnailFileName;
  private long processingTime;
  private String errorMessage;
  @CreatedDate
  private LocalDate createdAt;
  @LastModifiedDate
  private LocalDate updateAt;
}
