package com.barbariania.awsinfo;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazonaws.services.sqs.model.Message;
import com.barbariania.awsinfo.processor.SqsProcessor;
import com.barbariania.awsinfo.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SqsToSnsScheduler
{
    private final SqsProcessor sqsProcessor;
    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 * * ? * *") // every minute
    public void sendToSns() {
        final List<Message> sqsMessages = sqsProcessor.getSqsMessages();

        if (CollectionUtils.isEmpty(sqsMessages)) {
            return;
        }

        for (Message sqsMessage : sqsMessages) {
            String message = sqsMessage.getBody();
            subscriptionService.publishMessage(message);
        }

        sqsProcessor.deleteMessages(sqsMessages);
    }
}
