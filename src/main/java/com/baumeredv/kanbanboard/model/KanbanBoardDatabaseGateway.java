package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import org.springframework.stereotype.Component;

@Component("KanbanBoardDatabaseGateway")
public class KanbanBoardDatabaseGateway implements KanbanBoardGateway{

  @Override
  public PostIt addPostIt(String text, PostItStage stage) {
    return null;
  }

  @Override
  public Iterable<PostIt> allPostIts() {
    return null;
  }

  @Override
  public void deletePostIt(PostIt postIt) {

  }

  @Override
  public PostIt changePostItStage(PostIt postIt, PostItStage newStage)
      throws ThereIsNoSuchPostItException {
    return null;
  }

  @Override
  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    return null;
  }
}