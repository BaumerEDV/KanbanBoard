package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoPreviousStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component("KanbanBoardInMemoryGateway")
class KanbanBoardInMemoryGateway implements KanbanBoardGateway {

  private final ArrayList<PostIt> postIts;

  public KanbanBoardInMemoryGateway() {
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
    if (!wasPostItInList) {
      throw new ThereIsNoSuchPostItException("Tried to remove post it, but there was none");
    }
  }

  @Override
  public PostIt movePostItToNext(PostIt postIt)
      throws ThereIsNoNextStageException, ThereIsNoSuchPostItException {
    //REVIEW: another instance of why opening curly brackets belong in a new line ;)
    PostIt newPostIt = new PostIt(postIt.text(), postIt.stage().nextStage());
    boolean postItWasRemoved = postIts.remove(postIt);
    if (!postItWasRemoved) {
      throw new ThereIsNoSuchPostItException("Tried to remove post it, but there was none");
    }
    postIts.add(newPostIt);
    return newPostIt;
  }

  //TODO: refactor duplicate code in to next and to previous; move validation to model
  @Override
  public PostIt movePostItToPrevious(PostIt postIt)
      throws ThereIsNoPreviousStageException, ThereIsNoSuchPostItException {
    if (!postIts.contains(postIt)) {
      throw new ThereIsNoSuchPostItException("Tried to remove post it, but there was none");
    }
    PostIt newPostIt = new PostIt(postIt.text(), postIt.stage().previousStage());
    boolean postItWasRemoved = postIts.remove(postIt);
    postIts.add(newPostIt);
    return newPostIt;
  }
}
