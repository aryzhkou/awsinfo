package com.barbariania.awsinfo.processor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Service
public class AwsCredentialsProcessor {
  @Value("${AWS_ACCESS_KEY_ID}")
  private String accessKey;
  @Value("${AWS_SECRET_ACCESS_KEY}")
  private String secretKey;

  @Getter
  private AwsCredentialsProvider credentialsProvider = null;

  @PostConstruct
  public void setUp() {
    final AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
    credentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);
  }
}

