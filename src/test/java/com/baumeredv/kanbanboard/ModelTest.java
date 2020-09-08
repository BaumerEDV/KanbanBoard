package com.baumeredv.kanbanboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.baumeredv.kanbanboard.model.KanbanBoardModel;
import com.baumeredv.kanbanboard.model.PostIt;
import com.baumeredv.kanbanboard.model.PostItStage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ModelTest {

  private KanbanBoardModel model;

  @BeforeEach
  public void createModel() {
    ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
    this.model = context.getBean(KanbanBoardModel.class);
  }

  @Nested
  class AddingPostIts {

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

    @Test
    public void cannotAddNullPostIt(){
      assertThrows(NullPointerException.class, () -> model.addPostIt(null));
    }

    @Test
    public void newPostItsAreInTheBacklog(){
      PostIt postIt = model.addPostIt("some text");
      assertEquals(PostItStage.BACKLOG, postIt.stage());
    }
  }

  @Nested
  class DeletingPostIts {

    String postItText = "text of the post it to be deleted";

    @Test
    public void aDeletedPostItIsRemoved() {
      PostIt postIt = model.addPostIt(postItText);
      model.deletePostIt(postIt);
      assertFalse(isPostItInModel(postIt));
    }

    @Test
    public void deletingANonexistentPostItThrowsException() throws Exception {
      Constructor<PostIt> constructor = PostIt.class.getDeclaredConstructor(
          String.class,
          PostItStage.class);
      constructor.setAccessible(true);
      PostIt postIt = constructor.newInstance(postItText, PostItStage.BACKLOG);
      assertThrows(NoSuchElementException.class, () -> model.deletePostIt(postIt));
      /*REVIEW: should this use reflection or should this use mocking?
      or should this add a post it to get it? (the latter seems wrong because then the test
      incorrectly fails when adding is broken)
       */
    }

    @Test
    public void passingNullToDeleteThrowsException() {
      assertThrows(NullPointerException.class, () -> model.deletePostIt(null));
    }


  }

  private boolean isPostItInModel(PostIt addedPostIt) {
    Iterable<PostIt> allPostIts = model.AllPostIts();
    boolean isPostItInAllPostIts = false;
    for (PostIt postIt : allPostIts) {
      if (postIt.equals(addedPostIt)) {
        isPostItInAllPostIts = true;
        break;
      }
    }
    return isPostItInAllPostIts;
  }


}
