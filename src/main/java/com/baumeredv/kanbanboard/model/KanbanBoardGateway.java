package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import org.springframework.stereotype.Component;

@Component
interface KanbanBoardGateway {

  PostIt addPostIt(String text, PostItStage stage);

  Iterable<PostIt> allPostIts();

  void deletePostIt(PostIt postIt);

  PostIt movePostItToNext(PostIt postIt) throws ThereIsNoNextStageException;
}
