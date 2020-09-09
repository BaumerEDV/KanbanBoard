package com.baumeredv.kanbanboard.model.exceptions;

public class ThereIsNoPreviousStageException extends UnsupportedOperationException {

  public ThereIsNoPreviousStageException(String message) {
    super(message);
  }
}
