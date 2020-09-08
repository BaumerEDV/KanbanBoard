package com.baumeredv.kanbanboard.model;

import org.springframework.stereotype.Component;

@Component
interface KanbanBoardGateway {

  public PostIt addPostIt(String text);
  public Iterable<PostIt> allPostIts();

}
