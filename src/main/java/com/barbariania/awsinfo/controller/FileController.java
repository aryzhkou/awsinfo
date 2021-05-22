package com.barbariania.awsinfo.controller;

import com.barbariania.awsinfo.dto.FileMetadata;
import com.barbariania.awsinfo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;

  @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
    return new ResponseEntity<>(fileService.upload(file), HttpStatus.CREATED);
  }

//downloadFile

//  //get metadata for image/all images
//  // LUD, name, size in bytes, file extension
//  @RequestMapping(value = "file/{filename}/metadata", method = RequestMethod.GET)
//
//  // delete file, remove metadata
//  @RequestMapping(value = "file/{filename}", method = RequestMethod.DELETE)
}
