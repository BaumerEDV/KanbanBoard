package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoPreviousStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class KanbanBoardModel {

  static final PostItStage DEFAULT_STAGE = PostItStage.BACKLOG;

  @SuppressWarnings("FieldMayBeFinal")
  private KanbanBoardGateway gateway;

  @Autowired
  public KanbanBoardModel(
      @Qualifier("KanbanBoardInMemoryGateway") KanbanBoardGateway gateway) {
    this.gateway = gateway;
  }
  //REVIEW: here the autowiring a private property is a lot cleaner than wiring a constructor
  //REVIEW: oh, and another instance that shows opening curly brackets belong in a new line

  public PostIt addPostIt(String text) throws IllegalArgumentException {
    if (text == null) {
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

  public PostIt movePostItToNext(PostIt postIt)
      throws ThereIsNoSuchPostItException, ThereIsNoNextStageException {
    PostItStage newStage = postIt.stage().nextStage();
    return gateway.changePostItStage(postIt, newStage);
  }

  public PostIt movePostItToPrevious(PostIt postIt)
      throws ThereIsNoSuchPostItException, ThereIsNoPreviousStageException {
    PostItStage newStage = postIt.stage().previousStage();
    return gateway.changePostItStage(postIt, newStage);
  }

  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    return gateway.changePostItText(postIt, newText);
  }
}
