package com.baumeredv.kanbanboard.model.exceptions;

import java.util.NoSuchElementException;

public class ThereIsNoSuchPostItException extends NoSuchElementException {

  public ThereIsNoSuchPostItException(String message) {
    super(message);
  }
}
