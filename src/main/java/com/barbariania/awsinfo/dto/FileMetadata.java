package com.barbariania.awsinfo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FileMetadata {
  private long id;
  private Instant lastUpdateTime;
  private String name;
  private long size; // bytes
  private String extension;
}
