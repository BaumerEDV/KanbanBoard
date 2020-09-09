package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoPreviousStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;

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

  public PostItStage previousStage() throws ThereIsNoSuchPostItException{
    switch (this){
      case BACKLOG:
        throw new ThereIsNoPreviousStageException("Tried to get the previous stage of BACKLOG");
      case WIP:
        return BACKLOG;
      case TEST:
        return WIP;
      case DONE:
        return TEST;
      default:
        throw new RuntimeException("unreachable code");
    }
  }
}
