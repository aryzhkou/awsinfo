package com.barbariania.awsinfo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FileMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private LocalDateTime lastUpdateTime;
  private String name;
  private long size; // bytes
  private String extension;
  private String link;
}
