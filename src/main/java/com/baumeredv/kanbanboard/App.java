package com.baumeredv.kanbanboard;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.baumeredv.kanbanboard")
public class App {

  public static void main(String[] args) {
    System.out.print("Hello World");
  }

}
