package com.barbariania.awsinfo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbariania.awsinfo.controller.body.Subscription;
import com.barbariania.awsinfo.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("subscribe")
    public void subscribe(@RequestBody Subscription subscription) {
        subscriptionService.subscribe(subscription.getEmail());
    }

    @PostMapping("unsubscribe")
    public void unsubscribe(@RequestBody Subscription subscription) {
        subscriptionService.unsubscribe(subscription.getEmail(), subscription.getSubscriptionArn());
    }
}
