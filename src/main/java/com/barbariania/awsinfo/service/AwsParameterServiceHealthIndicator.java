package com.barbariania.awsinfo.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AwsParameterServiceHealthIndicator extends AbstractHealthIndicator
{
    private final AwsParameterService awsParameterService;

    private static final String PARAM_NAME = "testParameter0";
    private static final String FIELD_NAME_MESSAGE = "message";
    private static final String FIELD_NAME_DESCRIPTION = "description";

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            final String newValue = UUID.randomUUID().toString();
            awsParameterService.setParameter(PARAM_NAME, newValue, true, true);

            final String parameterValue = awsParameterService.getParameter(PARAM_NAME, true);

            if (!Objects.equals(newValue, parameterValue)) {
                builder.down()
                       .withDetail(FIELD_NAME_MESSAGE, "Parameter after set-get is not correct");
            } else {
                builder.up()
                       .withDetail(FIELD_NAME_MESSAGE, "" + PARAM_NAME + "'s value was set to '" + newValue + "' without problems");
            }
            // we can delete parameter now but it is no need,
            // also this parameter can be deleted manually without affecting health or application
        } catch (Exception exception) {
            builder.down()
                   .withDetail(FIELD_NAME_MESSAGE, exception.getMessage());
        } finally {
            builder.withDetail(FIELD_NAME_DESCRIPTION, "Hot-changeable parameters without system restart");
        }
    }
}
