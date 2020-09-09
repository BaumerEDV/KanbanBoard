package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;

public enum PostItStage {
  BACKLOG,
  WIP,
  TEST,
  DONE;

  public PostItStage nextStage() throws ThereIsNoNextStageException{
    switch (this){
      case BACKLOG:
        return WIP;
      case WIP:
        return TEST;
      case TEST:
        return DONE;
      case DONE:
        throw new ThereIsNoNextStageException("Tried to get the next stage of DONE");
      default:
        throw new RuntimeException("unreachable code");
    }
  }
}
