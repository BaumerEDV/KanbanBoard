package com.baumeredv.kanbanboard;

import com.baumeredv.kanbanboard.model.KanbanBoardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan("com.baumeredv.kanbanboard")
@Component
public class App {

  private KanbanBoardModel model;

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
    context.getBean(App.class);
  }

  @Autowired
  public App(KanbanBoardModel model) {
    this.model = model;
  }
  /*REVIEW: discuss Autowired constructor with boiler plate code vs no-parameter constructor and
  autowiring private properties
   */

}
