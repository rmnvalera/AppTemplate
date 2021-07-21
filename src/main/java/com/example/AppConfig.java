package com.example;

import com.example.service.configuration.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class AppConfig implements Configuration {

  @JsonProperty
  @NotNull
  private String name;

  @JsonProperty
  @Max(50)
  private int age;

  public String getName() {
    return name;
  }
}
