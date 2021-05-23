package com.barbariania.awsinfo.processor;

import javassist.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileProcessor {
  String upload(MultipartFile file) throws IOException;

  byte[] download(String filename) throws NotFoundException, IOException;

  void deleteByName(String filename) throws IOException;
}
