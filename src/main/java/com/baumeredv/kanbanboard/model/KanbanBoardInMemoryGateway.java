package com.baumeredv.kanbanboard.model;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component("KanbanBoardInMemoryGateway")
class KanbanBoardInMemoryGateway implements KanbanBoardGateway{

  private final ArrayList<PostIt> postIts;

  public KanbanBoardInMemoryGateway(){
    postIts = new ArrayList<>();
  }

  @Override
  public PostIt addPostIt(String text, PostItStage stage) {
    PostIt postIt = new PostIt(text, stage);
    this.postIts.add(postIt);
    return postIt;
  }

  @Override
  public Iterable<PostIt> allPostIts() {
    return new ArrayList<>(postIts);
  }

  @Override
  public void deletePostIt(PostIt postIt) {
    boolean wasPostItInList = postIts.remove(postIt);
    if(!wasPostItInList) {
      throw new NoSuchElementException("Tried to delete nonexistent post it from the post it list");
    }
  }
}
