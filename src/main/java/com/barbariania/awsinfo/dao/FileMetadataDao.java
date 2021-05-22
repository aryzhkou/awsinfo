package com.barbariania.awsinfo.dao;

import com.barbariania.awsinfo.dto.FileMetadata;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
public class FileMetadataDao {
  public FileMetadata save(String filename, long size) {
    Instant instantTime = Instant.now();
    LocalDateTime utcDateTime = instantTime.atZone(ZoneOffset.UTC).toLocalDateTime();

    FileMetadata fileMetadata = FileMetadata.builder()
        .name(filename)
        .size(size)
        .extension(getExtension(filename))
        .lastUpdateTime(instantTime)
        .build();

    long id = 4L; //fixme add db operation
    fileMetadata.setId(id);

    return fileMetadata;
  }

  private String getExtension(String filename) {
    final int firstExtensionSymbolPosition = filename.lastIndexOf(".") + 1;
    return firstExtensionSymbolPosition > 0 ? filename.substring(firstExtensionSymbolPosition) : null;
  }
}
