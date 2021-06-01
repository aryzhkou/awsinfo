package com.barbariania.awsinfo.controller.body;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subscription
{
    private String email;
    private String subscriptionArn;
}
