package com.barbariania.awsinfo.dao;

import com.barbariania.awsinfo.dto.FileMetadata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileMetadataRepository extends CrudRepository<FileMetadata, Long> {
  FileMetadata findByName(String name);

  /**
   * @return latest uploaded file metadata
   */
  FileMetadata findTopByOrderByIdDesc();

  List<FileMetadata> findAll();

  void deleteByName(String name);
}
