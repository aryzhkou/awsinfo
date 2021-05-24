package com.barbariania.awsinfo.controller;

import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.service.FileService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;

  @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
    return new ResponseEntity<>(fileService.upload(file), HttpStatus.CREATED);
  }

  @GetMapping(value = "download")
  public void downloadFile(@RequestParam String filename, HttpServletResponse response) throws IOException, NotFoundException {
    final FileMetadata fileMetadata = fileService.getMetadataByFilename(filename);
    if (fileMetadata == null) {
      throw new NotFoundException("File '" + filename + "' not exists in database");
    }
    response.setContentType("image/" + fileMetadata.getExtension());
    response.addHeader("Content-Disposition", "attachment; filename=" + filename);
    response.getOutputStream().write(fileService.download(filename));
    response.getOutputStream().flush();
  }

  /**
   * @return metadata by filename if provided, otherwise return latest uploaded file metadata
   */
  @GetMapping("metadata")
  public ResponseEntity getFilesMetadata(@RequestParam(required = false) String filename) {
    if (filename == null) {
      return ResponseEntity.of(Optional.of(fileService.getAllFilesMetadata()));
    }
    //if filename="" then will be returned latest file metadata
    final FileMetadata foundMetadata = fileService.getMetadataByFilename(filename);
    if (foundMetadata == null) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(foundMetadata, HttpStatus.OK);
  }

  @DeleteMapping(value = "{filename}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteByName(@PathVariable String filename) {
    fileService.delete(filename);
  }
}
