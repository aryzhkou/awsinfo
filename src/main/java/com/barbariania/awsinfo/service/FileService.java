package com.barbariania.awsinfo.service;

import com.barbariania.awsinfo.dao.FileMetadataDao;
import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.exception.FileStorageException;
import com.barbariania.awsinfo.processor.FileProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {
  private final FileProcessor fileProcessor;
  private final FileMetadataDao fileMetadataDao;

  public FileMetadata upload(MultipartFile file) {
    try {
      fileProcessor.upload(file);
      return fileMetadataDao.save(file.getOriginalFilename(), file.getSize());
    } catch (IOException e) {
      e.printStackTrace();
      //fixme delete from s3 if error response from db
      throw new FileStorageException("Could not store file " + file.getOriginalFilename());
    }
  }
}
