package com.vs.video_stream.request;

import lombok.Builder;

@Builder
public record AuthDTO(
  String email,
  String password
) {}
