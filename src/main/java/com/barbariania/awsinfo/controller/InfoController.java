package com.barbariania.awsinfo.controller;

import com.amazonaws.util.EC2MetadataUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
  @RequestMapping({"/", "api/info"})
  public ResponseEntity<EC2MetadataUtils.InstanceInfo> getInstanceInfo() {
    final EC2MetadataUtils.InstanceInfo instanceInfo = EC2MetadataUtils.getInstanceInfo();
    if (instanceInfo == null) {
      throw new RuntimeException("AWS returned no instanceInfo, check machine logs");
    }
    return new ResponseEntity<>(instanceInfo, HttpStatus.OK);
  }
}
