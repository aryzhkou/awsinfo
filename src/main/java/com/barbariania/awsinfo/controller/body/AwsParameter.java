package com.barbariania.awsinfo.controller.body;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsParameter {
  @NotNull(message = "'key' field is required")
  @Size(min = 2, max = 255, message = "'key' field must be of [2-10] length")
  private String key;
  @NotNull(message = "'value' field is required")
  @Size(min = 2, max = 255, message = "'value' field must be of [2-255] length")
  private String value;
  private boolean secured;
  private boolean overwrite;
}
