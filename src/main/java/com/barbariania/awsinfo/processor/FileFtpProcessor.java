package com.barbariania.awsinfo.processor;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

//@Component //uncomment to use it and comment FileAwsS3Processor
@RequiredArgsConstructor
public class FileFtpProcessor implements FileProcessor {
  @Value("${app.upload.dir:${user.home}}")
  private String uploadDir;

  public String upload(MultipartFile file) throws IOException {
    Path copyLocation = Paths.get(uploadDir
        + File.separator + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
    Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

    return copyLocation.toString();
  }

  @Override
  public byte[] download(String filename) throws NotFoundException, IOException {
    Path filePath = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(filename)));
    if (Files.exists(filePath)) {
      return Files.readAllBytes(filePath);
    }
    throw new NotFoundException("No file with such name exist");
  }

  @Override
  public void deleteByName(String filename) throws IOException {
    Path filePath = Paths.get(uploadDir + File.separator + StringUtils.cleanPath(Objects.requireNonNull(filename)));
    Files.delete(filePath);
  }
}
