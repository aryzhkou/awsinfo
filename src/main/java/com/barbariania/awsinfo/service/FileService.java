package com.barbariania.awsinfo.service;

import com.barbariania.awsinfo.dao.FileMetadataRepository;
import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.exception.FileStorageException;
import com.barbariania.awsinfo.processor.FileProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
  private final FileProcessor fileProcessor;
  private final FileMetadataRepository fileMetadataRepository;

  public FileMetadata upload(MultipartFile file) {
    try {
      String filePath = fileProcessor.upload(file);

      return save(file.getOriginalFilename(), file.getSize(), filePath);
    } catch (IOException e) {
      e.printStackTrace();
      //fixme delete from s3 if error response from db
      throw new FileStorageException("Could not store file " + file.getOriginalFilename());
    }
  }

  public MultipartFile download(String filename) {
    return null; //todo fixme
  }

  public List<FileMetadata> getAllFilesMetadata() {
    return fileMetadataRepository.findAll();
  }

  public FileMetadata getMetadataByFilename(String name) {
    return name == null
        ? fileMetadataRepository.findTopByOrderByIdDesc() //return latest
        : fileMetadataRepository.findByName(name);
  }

  public void delete(String name) {
    //todo implement
  }

  private FileMetadata save(String filename, long size, String path) {
    Instant instantTime = Instant.now();
    LocalDateTime utcDateTime = instantTime.atZone(ZoneOffset.UTC).toLocalDateTime();

    FileMetadata fileMetadata = new FileMetadata();
    fileMetadata.setName(filename);
    fileMetadata.setSize(size);
    fileMetadata.setExtension(getExtension(filename));
    fileMetadata.setLastUpdateTime(utcDateTime);
    fileMetadata.setLink(path);

    fileMetadata = fileMetadataRepository.save(fileMetadata);

    return fileMetadata;
  }

  private String getExtension(String filename) {
    final int firstExtensionSymbolPosition = filename.lastIndexOf(".") + 1;
    return firstExtensionSymbolPosition > 0 ? filename.substring(firstExtensionSymbolPosition) : null;
  }
}
