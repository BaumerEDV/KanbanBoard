package com.baumeredv.kanbanboard.model;

public final class PostIt {

  private final String _text;

  PostIt(String text) {
    this._text = text;
  }

  public String text(){
    return this._text;
  }
}
