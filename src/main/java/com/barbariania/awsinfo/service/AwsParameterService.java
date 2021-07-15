package com.barbariania.awsinfo.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.barbariania.awsinfo.processor.AwsCredentialsProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;
import software.amazon.awssdk.services.ssm.model.PutParameterResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsParameterService {
  private final AwsCredentialsProcessor credentialsProcessor;

  private SsmClient ssmClient = null;

  @PostConstruct
  public void finishInit() {
    Region region = Region.EU_WEST_2;
    ssmClient = SsmClient.builder()
                         .credentialsProvider(credentialsProcessor.getCredentialsProvider())
                         .region(region)
                         .build();
  }

  public void setParameter(String parameterName, String value, boolean isSecured, boolean needToOwerwrite) {
    final PutParameterRequest putParameterRequest = PutParameterRequest.builder()
                                                                       .name(parameterName)
                                                                       .value(value)
                                                                       .overwrite(needToOwerwrite)
                                                                       .type(isSecured ? ParameterType.SECURE_STRING : ParameterType.STRING)
                                                                       .build();
    final PutParameterResponse putParameterResponse = ssmClient.putParameter(putParameterRequest);
    log.debug("Set a parameter key={} with response: {}", parameterName, putParameterResponse.toString());
  }

  public String getParameter(String parameterName, boolean needDecryption) {
    GetParameterRequest parameterRequest = GetParameterRequest.builder()
                                                              .withDecryption(needDecryption)
                                                              .name(parameterName)
                                                              .build();

    GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
    final String value = parameterResponse.parameter().value();
    log.debug("Parameter got: key={}, decrypted={}, value={}", parameterName, needDecryption, value);
    return value;
  }
}
