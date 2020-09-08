package com.baumeredv.kanbanboard.model;

import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component("KanbanBoardInMemoryGateway")
class KanbanBoardInMemoryGateway implements KanbanBoardGateway{

  private final ArrayList<PostIt> postIts;

  public KanbanBoardInMemoryGateway(){
    postIts = new ArrayList<>();
  }

  @Override
  public PostIt addPostIt(String text) {
    PostIt postIt = new PostIt(text);
    this.postIts.add(postIt);
    return postIt;
  }

  @Override
  public Iterable<PostIt> allPostIts() {
    return new ArrayList<>(postIts);
  }
}
