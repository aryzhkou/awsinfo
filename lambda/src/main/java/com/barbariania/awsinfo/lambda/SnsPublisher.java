package com.barbariania.awsinfo.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.util.CollectionUtils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

public class SnsPublisher implements RequestStreamHandler {
    private static final String SNS_TOPIC = "arn:aws:sns:eu-west-2:327049370098:awsinfo-uploads-notification-topic";
    private static final String QUEUE_URL = "https://sqs.eu-west-2.amazonaws.com/327049370098/awsinfo-uploads-notification-queue";

    private static final SnsClient SNS_CLIENT = getSnsClient();
    private static final AmazonSQS SQS_CLIENT = getSqsClient();

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            JSONObject event = (JSONObject) new JSONParser().parse(reader);
            System.out.println("Request body: " + event);
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        final List<Message> sqsMessages = getMessagesFromSqs(10);
        System.out.println("Returned " + sqsMessages.size() + " messages from SQS");

        if (CollectionUtils.isNullOrEmpty(sqsMessages)) {
            System.out.println("Cancel further execution: got no messages to send.");
            return;
        }

        try {
            publishMessageToSns(formAMessage(sqsMessages));
        } catch (SnsException exception) {
            System.err.println(exception.awsErrorDetails().errorCode() + " " + exception.awsErrorDetails().errorMessage());
            throw exception;
        }

        deleteMessagesFromSqs(sqsMessages);
    }

    private String formAMessage(List<Message> sqsMessages) {
        StringBuilder message = new StringBuilder("The following images were uploaded:");
        int i = 1;
        for (Message sqsMessage : sqsMessages) {
            message.append("\n").append(i++).append(": ").append(sqsMessage.getBody());
        }
        return message.toString();
    }

    /** SQS */
    private static AmazonSQS getSqsClient() {
        final AmazonSQSClientBuilder awsSqsBuilder = AmazonSQSClientBuilder.standard();
        awsSqsBuilder.setRegion(Region.EU_WEST_2.toString());
        final AmazonSQS amazonSQS = awsSqsBuilder.build();
        System.out.println("Sqs client is configured " + amazonSQS);
        return amazonSQS;
    }

    private List<Message> getMessagesFromSqs(int maxMessagesCount) {
        boolean checkSqsAgain = true;
        int maxAttemptCount = 5;
        int attemptNumber = 1;
        Set<Message> messages = new HashSet<>();
        //workaround to get all messages from SQS - for some reason it still gets only by 1 message
        while(checkSqsAgain && maxAttemptCount >= attemptNumber) {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
            receiveMessageRequest.setQueueUrl(QUEUE_URL);
            receiveMessageRequest.setMaxNumberOfMessages(maxMessagesCount);
            receiveMessageRequest.setVisibilityTimeout(30); // that way we won't receive messages twice
            List<Message> returnedMessages = SQS_CLIENT.receiveMessage(receiveMessageRequest).getMessages();
            if (CollectionUtils.isNullOrEmpty(returnedMessages)) {
                checkSqsAgain = false;
            }
            System.out.println("Attempt " + attemptNumber + ": got " + returnedMessages.size() + " messages");
            messages.addAll(returnedMessages);
            attemptNumber++;
        }
        return new ArrayList<>(messages);
    }

    private void deleteMessagesFromSqs(List<Message> messages) {
        for (Message m : messages) {
            SQS_CLIENT.deleteMessage(QUEUE_URL, m.getReceiptHandle());
        }
    }

    /** SNS */
    private static SnsClient getSnsClient() {
        final SnsClient snsClientVal = SnsClient.builder()
                                                .region(Region.EU_WEST_2)
                                                .build();
        System.out.println("Sns client is configured " + snsClientVal);
        return snsClientVal;
    }

    public void publishMessageToSns(String message) throws SnsException {
        PublishRequest request = PublishRequest.builder()
                                               .message(message)
                                               .topicArn(SNS_TOPIC)
                                               .build();

        PublishResponse result = SNS_CLIENT.publish(request);
        System.out.println(result.messageId() + ": Message sent. Status was " + result.sdkHttpResponse().statusCode());
    }
}