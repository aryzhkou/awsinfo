package com.barbariania.awsinfo.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.barbariania.awsinfo.processor.AwsCredentialsProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService //Aws Sns
{
    private SnsClient snsClient = null;
    private final AwsCredentialsProcessor awsCredentialsProcessor;

    private static final Map<String, String> SUBSCRIPTIONS = new HashMap<>();

    private static final String SNS_TOPIC = "arn:aws:sns:eu-west-2:327049370098:awsinfo-uploads-notification-topic";

    @PostConstruct
    public void finishInit() {
        snsClient = getSnsClient();
    }

    public void subscribe(String email) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                                                       .protocol("email")
                                                       .endpoint(email)
                                                       .returnSubscriptionArn(true)
                                                       .topicArn(SNS_TOPIC)
                                                       .build();

            SubscribeResponse result = snsClient.subscribe(request);
            SUBSCRIPTIONS.put(email, result.subscriptionArn());
            log.info("Subscription ARN: " + result.subscriptionArn() + "\n\n Status was " + result.sdkHttpResponse().statusCode());
        } catch (SnsException exception) {
            exception.printStackTrace();
            log.error(exception.awsErrorDetails().errorMessage());
            throw exception;
        }

    }

    public void unsubscribe(String email, String subscriptionArn) {
        try {
            if (subscriptionArn == null) {
                subscriptionArn = SUBSCRIPTIONS.get(email);
            }
            UnsubscribeRequest request = UnsubscribeRequest.builder()
                                                           .subscriptionArn(subscriptionArn)
                                                           .build();

            UnsubscribeResponse result = snsClient.unsubscribe(request);

            log.info("\n\nStatus was " + result.sdkHttpResponse().statusCode()
                               + "\n\nSubscription was removed for " + request.subscriptionArn());

        } catch (SnsException e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public void publishMessage(String message) throws SnsException {
        PublishRequest request = PublishRequest.builder()
                                               .message(message)
                                               .topicArn(SNS_TOPIC)
                                               .build();

        PublishResponse result = snsClient.publish(request);
        log.info(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
    }

    private SnsClient getSnsClient() {
        return SnsClient.builder()
                        .credentialsProvider(awsCredentialsProcessor.getCredentialsProvider())
                        .region(Region.EU_WEST_2)
                        .build();
    }
}
