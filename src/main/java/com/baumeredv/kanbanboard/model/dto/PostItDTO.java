package com.baumeredv.kanbanboard.model.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PostItDTO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String text;

  private String stage;

  public PostItDTO() {
    super();
  }

  public PostItDTO(String text, String stage) {
    this.text = text;
    this.stage = stage;
  }

  public PostItDTO(int id, String text, String stage) {
    this.id = id;
    this.text = text;
    this.stage = stage;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }
}
