package com.baumeredv.kanbanboard.model;

public final class PostIt {

  private final String _text;
  private final PostItStage _stage;

  PostIt(String text, PostItStage stage) {
    _text = text;
    _stage = stage;
  }

  public String text() {
    return this._text;
  }

  public PostItStage stage() {
    return this._stage;
  }

  public boolean equals(Object object) {
    if (!(object instanceof PostIt)) {
      return false;
    }

    PostIt other = (PostIt) object;
    return this._stage == other._stage && this._text.equals(other._text);
  }
}
