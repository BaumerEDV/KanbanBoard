package com.baumeredv.kanbanboard.model;


import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.util.ArrayList;
import org.springframework.context.annotation.Primary;
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
  public PostIt changePostItStage(PostIt postIt, PostItStage newStage)
      throws ThereIsNoSuchPostItException {
    if (!postIts.contains(postIt)) {
      throw new ThereIsNoSuchPostItException("Tried to remove post it, but there was none");
    }
    PostIt newPostIt = new PostIt(postIt.text(), newStage);
    postIts.remove(postIt);
    postIts.add(newPostIt);
    return newPostIt;
  }

  @Override
  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    if(!postIts.contains(postIt)){
      throw new ThereIsNoSuchPostItException("Tried to change post it, but there was none");
    }
    PostIt newPostIt = new PostIt(newText, postIt.stage());
    postIts.remove(postIt);
    postIts.add(newPostIt);
    return newPostIt;
  }
}
