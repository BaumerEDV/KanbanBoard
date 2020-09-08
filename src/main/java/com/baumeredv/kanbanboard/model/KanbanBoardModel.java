package com.baumeredv.kanbanboard.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KanbanBoardModel {

  @SuppressWarnings("FieldMayBeFinal")
  private KanbanBoardGateway gateway;

  @Autowired
  public KanbanBoardModel(KanbanBoardGateway gateway) {
    this.gateway = gateway;
  }

  public PostIt addPostIt(String text) throws IllegalArgumentException {
    if (text.equals("")) {
      throw new IllegalArgumentException("The text of a post it note must not be empty");
    }
    return gateway.addPostIt(text);
  }

  public Iterable<PostIt> AllPostIts() {
    return gateway.allPostIts();
  }

}
