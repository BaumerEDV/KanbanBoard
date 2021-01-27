package com.baumeredv.kanbanboard.model.dto;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class PostItDTO {

  @EmbeddedId
  private PostItId postItId;

  public PostItDTO() {
    super();
  }

  public PostItDTO(PostItId postItId) {
    this.postItId = postItId;
  }

  public PostItId getPostItId() {
    return postItId;
  }

  public void setPostItId(PostItId postItId) {
    this.postItId = postItId;
  }
}
