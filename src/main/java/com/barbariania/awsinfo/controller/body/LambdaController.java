package com.barbariania.awsinfo.controller.body;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbariania.awsinfo.service.AwsLambdaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/lambda")
@RequiredArgsConstructor
public class LambdaController
{
    private final AwsLambdaService lambdaService;

    @RequestMapping
    public ResponseEntity<String> callLambda() {
        String function = "arn:aws:lambda:eu-west-2:327049370098:function:awsinfo-uploads-batch-notifier";
        return new ResponseEntity<>(lambdaService.runLambda(function), HttpStatus.OK);
    }
}
