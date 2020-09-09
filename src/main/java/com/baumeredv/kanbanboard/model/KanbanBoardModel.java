package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KanbanBoardModel {

  static final PostItStage DEFAULT_STAGE = PostItStage.BACKLOG;

  @SuppressWarnings("FieldMayBeFinal")
  private KanbanBoardGateway gateway;

  @Autowired
  public KanbanBoardModel(KanbanBoardGateway gateway) {
    this.gateway = gateway;
  }

  public PostIt addPostIt(String text) throws IllegalArgumentException {
    if (text == null){
      throw new NullPointerException("Post it was to be created, but null was given as text");
    }
    if (text.equals("")) {
      throw new IllegalArgumentException("The text of a post it note must not be empty");
    }
    return gateway.addPostIt(text, DEFAULT_STAGE);
  }

  public Iterable<PostIt> AllPostIts() {
    return gateway.allPostIts();
  }

  public void deletePostIt(PostIt postIt) {
    if (postIt == null) {
      throw new NullPointerException("A post it was to be deleted but null was given");
    }
    gateway.deletePostIt(postIt);
  }

  public PostIt movePostItToNext(PostIt postIt) throws ThereIsNoNextStageException {
    return gateway.movePostItToNext(postIt);
  }

  public PostIt movePostItToPrevious(PostIt postIt) {
    return gateway.movePostItToPrevious(postIt);
  }
}
