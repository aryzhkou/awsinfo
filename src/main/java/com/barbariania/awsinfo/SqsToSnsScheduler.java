package com.barbariania.awsinfo;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazonaws.services.sqs.model.Message;
import com.barbariania.awsinfo.processor.SqsProcessor;
import com.barbariania.awsinfo.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sns.model.SnsException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsToSnsScheduler
{
    private final SqsProcessor sqsProcessor;
    private final SubscriptionService subscriptionService;

//    @Scheduled(cron = "0 * * ? * *") // every minute
    public void sendToSns() {
        final List<Message> sqsMessages = sqsProcessor.getSqsMessages();

        if (CollectionUtils.isEmpty(sqsMessages)) {
            return;
        }

        StringBuilder message = new StringBuilder("The following images were uploaded:");
        int i = 1;
        for (Message sqsMessage : sqsMessages) {
            message.append("\n").append(i++).append(": ").append(sqsMessage.getBody());
        }
        try {
            subscriptionService.publishMessage(message.toString());
        } catch (SnsException exception) {
            log.error(exception.awsErrorDetails().errorCode() + " " + exception.awsErrorDetails().errorMessage());
            // we don't want to delete messages in case the message wasn't published successfully
            throw exception;
        }

        sqsProcessor.deleteMessages(sqsMessages);
    }
}
