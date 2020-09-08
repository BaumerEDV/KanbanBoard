package com.baumeredv.kanbanboard.model;

import org.springframework.stereotype.Component;

@Component
interface KanbanBoardGateway {

  PostIt addPostIt(String text, PostItStage stage);

  Iterable<PostIt> allPostIts();

  void deletePostIt(PostIt postIt);
}
