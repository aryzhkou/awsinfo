package com.barbariania.awsinfo.processor;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileProcessor {
  void upload(MultipartFile file) throws IOException;
}
