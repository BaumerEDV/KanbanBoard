package com.baumeredv.kanbanboard.model.dto;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class PostIt {

  @EmbeddedId
  private PostItId postItId;

  public PostIt(PostItId postItId) {
    this.postItId = postItId;
  }

  public PostItId getPostItId() {
    return postItId;
  }

  public void setPostItId(PostItId postItId) {
    this.postItId = postItId;
  }
}
