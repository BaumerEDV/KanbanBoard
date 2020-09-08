package com.baumeredv.kanbanboard.model;

import org.springframework.stereotype.Component;

@Component
interface KanbanBoardGateway {

  PostIt addPostIt(String text);

  Iterable<PostIt> allPostIts();

}
