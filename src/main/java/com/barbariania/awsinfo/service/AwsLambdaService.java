package com.barbariania.awsinfo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwsLambdaService
{
    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKey;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    private AWSLambda lambdaClient = null;

    @PostConstruct
    public void finishInit() {
        AWSCredentialsProvider awsCredentialsProvider =
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));

        lambdaClient = AWSLambdaClientBuilder.standard()
                                                    .withCredentials(awsCredentialsProvider)
                                                    .withRegion(Regions.EU_WEST_2).build();
    }

    public String runLambda(String functionName) {
        InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(functionName)
            .withPayload("{\n"
                         + "\"detail-type\": \"RunFromApplication\""
                         + "\n}");

        try {
            InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);

            String answer = invokeResult.getStatusCode() + " : " + new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            log.info(answer);
            return answer;
        } catch (ServiceException e) {
            log.error("Cannot run AWS Lambda: ", e);
            throw e;
        }
    }
}
