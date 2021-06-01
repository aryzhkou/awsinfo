package com.barbariania.awsinfo.processor;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.barbariania.awsinfo.dto.FileMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;


@Component
@RequiredArgsConstructor
@Slf4j
public class SqsProcessor
{
    @Value("${AWS_ACCESS_KEY_ID}")
    private String awsAccessKey;
    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String awsSecretKey;

    private final ObjectMapper objectMapper;

    private AmazonSQS sqs = null;

    @PostConstruct
    public void finishInit() {
        AWSCredentialsProvider awsCredentialsProvider =
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        final AmazonSQSClientBuilder awsSqsBuilder = AmazonSQSClientBuilder.standard();
        awsSqsBuilder.setCredentials(awsCredentialsProvider);
        awsSqsBuilder.setRegion(Region.EU_WEST_2.toString());
        sqs = awsSqsBuilder.build();
    }

    private static final String QUEUE_URL = "https://sqs.eu-west-2.amazonaws.com/327049370098/awsinfo-uploads-notification-queue";

    public void sendToSqs(FileMetadata fileMetadata) {
        String message = "File has been uploaded + " + fileMetadata.getName() + ", link=" + fileMetadata.getLink();
        try
        {
            message = objectMapper.writeValueAsString(fileMetadata);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        SendMessageRequest send_msg_request = new SendMessageRequest()
            .withQueueUrl(QUEUE_URL)
            .withMessageBody(message)
            .withDelaySeconds(5);
        final SendMessageResult sendMessageResult = sqs.sendMessage(send_msg_request);
        log.info(sendMessageResult.getSdkHttpMetadata().getHttpStatusCode() + " " + sendMessageResult.toString());
    }

    public List<Message> getSqsMessages() {
        return sqs.receiveMessage(QUEUE_URL).getMessages();
    }

    public void deleteMessages(List<Message> messages) {
        for (Message m : messages) {
            sqs.deleteMessage(QUEUE_URL, m.getReceiptHandle());
        }
    }
}
