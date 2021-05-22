package com.barbariania.awsinfo.processor;

import com.barbariania.awsinfo.dao.FileMetadataDao;
import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FileFtpProcessor implements FileProcessor {
  @Value("${app.upload.dir:${user.home}}")
  private String uploadDir;

  public void upload(MultipartFile file) throws IOException {
    Path copyLocation = Paths.get(uploadDir
        + File.separator + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
    Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
  }
}
