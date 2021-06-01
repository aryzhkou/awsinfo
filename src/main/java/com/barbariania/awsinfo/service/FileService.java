package com.barbariania.awsinfo.service;

import com.barbariania.awsinfo.dao.FileMetadataRepository;
import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.exception.FileStorageException;
import com.barbariania.awsinfo.processor.FileProcessor;
import com.barbariania.awsinfo.processor.SqsProcessor;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.barbariania.awsinfo.processor.FileProcessorHelper.getExtension;

@Service
@RequiredArgsConstructor
public class FileService {
  private final FileProcessor fileProcessor;
  private final FileMetadataRepository fileMetadataRepository;
  private final SqsProcessor sqsProcessor;

  @Value("${server.port}")
  private String serverPort;

  public FileMetadata upload(MultipartFile file) {
    try {//todo replace file and metadata if already exists or update metadata in db?
      String filePath = fileProcessor.upload(file); //save and return ftpFilePath

      FileMetadata fileMetadata = save(file.getOriginalFilename(), file.getSize());

      sqsProcessor.sendToSqs(fileMetadata);
      return fileMetadata;
    } catch (IOException e) {
      e.printStackTrace();
      //fixme delete from s3 if error response from db
      throw new FileStorageException("Could not store file " + file.getOriginalFilename());
    }
  }

  public byte[] download(String filename) throws IOException, NotFoundException {
    return fileProcessor.download(filename);
  }

  public List<FileMetadata> getAllFilesMetadata() {
    try {
      return fileMetadataRepository.findAll();
    } catch (GenericJDBCException exception) {
      exception.printStackTrace();
      throw new FileStorageException("Cannot get files metadata : " + exception.getLocalizedMessage());
    }
  }

  public FileMetadata getMetadataByFilename(String name) {
    return ObjectUtils.isEmpty(name)
        ? fileMetadataRepository.findTopByOrderByIdDesc() //return latest
        : fileMetadataRepository.findByName(name);
  }

  public void delete(String name) {
    try {
      fileProcessor.deleteByName(name);
      fileMetadataRepository.deleteByName(name);
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new FileStorageException("Could not delete file: " + exception.getMessage());
    }
  }

  private FileMetadata save(String filename, long size) throws UnknownHostException {
    Instant instantTime = Instant.now();
    LocalDateTime utcDateTime = instantTime.atZone(ZoneOffset.UTC).toLocalDateTime();

    FileMetadata fileMetadata = new FileMetadata();
    fileMetadata.setName(filename);
    fileMetadata.setSize(size);
    fileMetadata.setExtension(getExtension(filename));
    fileMetadata.setLastUpdateTime(utcDateTime);
    String baseUrl = InetAddress.getLocalHost().getHostName() + ":" + serverPort;
    fileMetadata.setLink(baseUrl + "/api/files/download?filename=" + filename);

    fileMetadata = fileMetadataRepository.save(fileMetadata);

    return fileMetadata;
  }
}
