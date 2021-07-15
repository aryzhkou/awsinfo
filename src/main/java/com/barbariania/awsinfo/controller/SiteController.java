package com.barbariania.awsinfo.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.barbariania.awsinfo.controller.body.AwsParameter;
import com.barbariania.awsinfo.service.AwsParameterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SiteController {
  private final AwsParameterService awsParameterService;

  @GetMapping("/")
  public RedirectView redirectWithUsingRedirectView(
      RedirectAttributes attributes) {
    attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
    return new RedirectView("landing.html");
  }

  @GetMapping("api/parameter")
  @ResponseBody
  public String getParameter(@RequestParam("param") String param,
                             @RequestParam(value = "decrypt", required = false, defaultValue = "false") boolean needDecryption) {
    return awsParameterService.getParameter(param, needDecryption);
  }

  @PostMapping("api/parameter")
  @ResponseBody
  public void setParameter(@Valid @RequestBody AwsParameter awsParameter) {
    awsParameterService.setParameter(awsParameter.getKey(), awsParameter.getValue(), awsParameter.isSecured(), awsParameter.isOverwrite());
  }
}
