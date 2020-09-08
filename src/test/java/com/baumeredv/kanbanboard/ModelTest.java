package com.baumeredv.kanbanboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.baumeredv.kanbanboard.model.KanbanBoardModel;
import com.baumeredv.kanbanboard.model.PostIt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ModelTest {

  private KanbanBoardModel model;

  @BeforeEach
  public void createModel() {
    ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
    this.model = context.getBean(KanbanBoardModel.class);
  }

  @Test
  public void canAddPostIt() throws IllegalArgumentException {
    PostIt addedPostIt = model.addPostIt("a test post it");
    assertTrue(isPostItInModel(addedPostIt));
  }

  @Test
  public void addedPostItHasCorrectText() throws IllegalArgumentException {
    String text = "the test Text";
    PostIt addedPostIt = model.addPostIt(text);
    assertEquals(text, addedPostIt.text());
  }

  @Test
  public void canAddTwoPostIts() throws IllegalArgumentException {
    String text1 = "text of the first post it";
    String text2 = "a different text";
    PostIt firstPostIt = model.addPostIt(text1);
    PostIt secondPostIt = model.addPostIt(text2);
    assertTrue(isPostItInModel(firstPostIt));
    assertTrue(isPostItInModel(secondPostIt));
  }

  @Test
  public void cannotAddEmptyPostIt() {
    assertThrows(IllegalArgumentException.class, () -> model.addPostIt(""));
  }

  private boolean isPostItInModel(PostIt addedPostIt) {
    Iterable<PostIt> allPostIts = model.AllPostIts();
    boolean isAddedPostItInAllPostIts = false;
    for (PostIt postIt : allPostIts) {
      if (postIt.equals(addedPostIt)) {
        isAddedPostItInAllPostIts = true;
        break;
      }
    }
    return isAddedPostItInAllPostIts;
  }


}
