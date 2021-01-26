package com.baumeredv.kanbanboard.model.dto;

import javax.persistence.Embeddable;

@Embeddable
public class PostItId {

  private String text;
  private String stage; //https://stackoverflow.com/questions/229856/ways-to-save-enums-in-database

  public PostItId(String text, String stage) {
    this.text = text;
    this.stage = stage;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
