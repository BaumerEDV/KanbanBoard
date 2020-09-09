package com.baumeredv.kanbanboard.model.exceptions;

public class ThereIsNoNextStageException extends UnsupportedOperationException {

  public ThereIsNoNextStageException(String description) {
    super(description);
  }
}
