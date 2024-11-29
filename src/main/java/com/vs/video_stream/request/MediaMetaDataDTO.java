package com.vs.video_stream.request;

import com.vs.video_stream.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaMetaDataDTO {

  private Status status;
  private String folderName;
  private String originalFileName;
  private String processedFileName;
  private long processingTime;
  private String errorMessage;
}