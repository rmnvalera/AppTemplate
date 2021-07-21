package com.example;

import com.example.service.Application;

public class Main extends Application<AppConfig> {

  @Override
  public void run(AppConfig config) throws Exception {
    System.out.println();
  }

  public static void main(String[] args) throws Exception {
    new Main().run(args);
  }

}
