package com.baumeredv.kanbanboard;


import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppTest {

  @Test
  public void applicationStartsWithoutErrors() {
    App.main(new String[0]);
  }

}
