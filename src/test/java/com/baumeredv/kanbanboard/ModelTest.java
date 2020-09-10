package com.baumeredv.kanbanboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.baumeredv.kanbanboard.model.KanbanBoardModel;
import com.baumeredv.kanbanboard.model.PostIt;
import com.baumeredv.kanbanboard.model.PostItStage;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoNextStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoPreviousStageException;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

//REVIEW: These imports show that wild card imports *should* be used.

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
    public void cannotAddNullPostIt() {
      assertThrows(NullPointerException.class, () -> model.addPostIt(null));
    }

    @Test
    public void newPostItsAreInTheBacklog() {
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
      PostIt postIt = createPostItInstance(postItText);
      assertThrows(NoSuchElementException.class, () -> model.deletePostIt(postIt));
    }

    @Test
    public void passingNullToDeleteThrowsException() {
      assertThrows(NullPointerException.class, () -> model.deletePostIt(null));
    }


  }

  @Nested
  class MovingPostIts {

    private final String POST_IT_TEXT = "some text";
    private ArrayList<PostIt> temporaryPostIts;

    @BeforeEach
    public void setup() {
      temporaryPostIts = new ArrayList<>();
    }

    @Nested
    class MovingPostItsForward {

      private PostIt postIt;
      private PostIt movedPostIt;

      @BeforeEach
      public void setup() throws Exception {
        postIt = model.addPostIt(POST_IT_TEXT);
      }

      @Test
      public void movingOnceMeansWIP() {
        movedPostIt = model.movePostItToNext(postIt);
        temporaryPostIts.add(postIt);
        assertEquals(PostItStage.WIP, movedPostIt.stage());
      }

      @Test
      public void movingTwiceMeansTEST() {
        movedPostIt = postIt;
        for (int i = 0; i < 2; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToNext(movedPostIt);
        }
        assertEquals(PostItStage.TEST, movedPostIt.stage());
      }

      @Test
      public void movingThreeTimesMeansDONE() {
        movedPostIt = postIt;
        for (int i = 0; i < 3; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToNext(movedPostIt);
        }
        assertEquals(PostItStage.DONE, movedPostIt.stage());
      }

      @Test
      public void movingFourTimesThrowsError() {
        movedPostIt = postIt;
        for (int i = 0; i < 3; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToNext(movedPostIt);
        }
        assertThrows(ThereIsNoNextStageException.class, () -> model.movePostItToNext(movedPostIt));
      }

      @AfterEach
      public void movedPostItIsInModel() {
        if (movedPostIt != null) {
          assertTrue(isPostItInModel(movedPostIt));
        }
      }
    }

    @Nested
    class MovingPostItsBackward {

      private PostIt postIt;
      private PostIt movedPostIt;

      @BeforeEach
      public void setupADonePostIt() {
        postIt = model.addPostIt(POST_IT_TEXT);
        for (int i = 0; i < 3; i++) {
          postIt = model.movePostItToNext(postIt);
        }
        assertEquals(PostItStage.DONE, postIt.stage());
      }

      @Test
      public void movingBackOnceMeansTEST() {
        temporaryPostIts.add(postIt);
        movedPostIt = model.movePostItToPrevious(postIt);
        assertEquals(PostItStage.TEST, movedPostIt.stage());
      }

      @Test
      public void movingBackTwiceMeansWIP() {
        movedPostIt = postIt;
        for (int i = 0; i < 2; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToPrevious(movedPostIt);
        }
        assertEquals(PostItStage.WIP, movedPostIt.stage());
      }

      @Test
      public void movingBackThreeTimesMeansBACKLOG() {
        movedPostIt = postIt;
        for (int i = 0; i < 3; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToPrevious(movedPostIt);
        }
        assertEquals(PostItStage.BACKLOG, movedPostIt.stage());
      }

      @Test
      public void movingBackFourTimesThrows() {
        movedPostIt = postIt;
        for (int i = 0; i < 3; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToPrevious(movedPostIt);
        }
        assertThrows(ThereIsNoPreviousStageException.class,
            () -> model.movePostItToPrevious(movedPostIt));
      }

      @AfterEach
      public void movedPostItIsInModel() {
        assertTrue(isPostItInModel(movedPostIt));
      }

    }

    @Test
    public void movingNonexistentPostItForwardThrows() throws Exception {
      PostItStage currentStage = PostItStage.BACKLOG;
      for (int i = 0; i < 3; i++) {
        final PostIt postIt = createPostItInstance(POST_IT_TEXT, currentStage);
        assertThrows(ThereIsNoSuchPostItException.class, () -> model.movePostItToNext(postIt));
        currentStage = currentStage.nextStage();
      }
      final PostIt postIt = createPostItInstance(POST_IT_TEXT, currentStage);
      assertThrows(ThereIsNoNextStageException.class, () -> model.movePostItToNext(postIt));
    }

    @Test
    public void movingNonexistentPostItBackwardsThrows() throws Exception {
      PostItStage currentStage = PostItStage.DONE;
      for (int i = 0; i < 3; i++) {
        final PostIt postIt = createPostItInstance(POST_IT_TEXT, currentStage);
        assertThrows(ThereIsNoSuchPostItException.class, () -> model.movePostItToPrevious(postIt));
        currentStage = currentStage.previousStage();
      }
      final PostIt postIt = createPostItInstance(POST_IT_TEXT, currentStage);
      assertThrows(ThereIsNoPreviousStageException.class, () -> model.movePostItToPrevious(postIt));
    }

    @AfterEach
    public void oldPostItsAreNotInModel() {
      for (PostIt postIt : temporaryPostIts) {
        assertFalse(isPostItInModel(postIt));
      }
    }

  }

  @Nested
  class ChangingPostItText {

    private PostIt postIt;
    private final String OLD_TEXT = "old text";
    private final String NEW_TEXT = "new text";

    @BeforeEach
    public void setup() {
      postIt = model.addPostIt(OLD_TEXT);
    }

    @Test
    public void changingTextChangesText() {
      PostIt newPostIt = model.changePostItText(postIt, NEW_TEXT);
      assertEquals(NEW_TEXT, newPostIt.text());
    }

    @Test
    public void changingTextSavesChangedPostIt() {
      PostIt newPostIt = model.changePostItText(postIt, NEW_TEXT);
      assertTrue(isPostItInModel(newPostIt));
    }

    @Test
    public void changingTextRemovesOriginalPostIt() {
      model.changePostItText(postIt, NEW_TEXT);
      assertFalse(isPostItInModel(postIt));
    }

    @Test
    public void changingTextOfANonexistentPostItThrows() throws Exception {
      PostIt nonexistentPostIt = createPostItInstance(NEW_TEXT);
      assertThrows(ThereIsNoSuchPostItException.class,
          () -> model.changePostItText(nonexistentPostIt, NEW_TEXT + "2"));
    }

  }

  @Nested
  class PostItEquals {

    String postItText = "some text";
    PostIt postIt;

    @BeforeEach
    public void createPostIt() throws Exception {
      postIt = createPostItInstance(postItText);
    }

    @Test
    public void postItDoesNotEqualSomethingThatIsNotPostIt() {
      assertFalse(postIt.equals(1));
      assertFalse(postIt.equals("something"));
      assertFalse(postIt.equals(postItText));
    }

    @Test
    public void postItDoesNotEqualNull() {
      assertFalse(postIt.equals(null));
    }

    @Test
    public void postItEqualsItself() {
      assertTrue(postIt.equals(postIt));
    }

    @Test
    public void postItDoesNotEqualADifferentPostIt() throws Exception {
      PostIt otherPostIt = createPostItInstance(postItText + "2");
      assertFalse(postIt.equals(otherPostIt));
    }

  }


  private PostIt createPostItInstance(String text) throws Exception {
    return createPostItInstance(text, PostItStage.BACKLOG);
  }

  private PostIt createPostItInstance(String text, PostItStage stage) throws Exception {
    Constructor<PostIt> constructor = PostIt.class.getDeclaredConstructor(
        String.class,
        PostItStage.class);
    constructor.setAccessible(true);
    return constructor.newInstance(text, stage);
    /*REVIEW: should this use reflection or should this use mocking?
      or should this add a post it to get it? (the latter seems wrong because then the test
      incorrectly fails when adding is broken)
       */
  }

  private boolean isPostItInModel(PostIt postItInQuestion) {
    Iterable<PostIt> allPostIts = model.AllPostIts();
    boolean isPostItInAllPostIts = false;
    for (PostIt postIt : allPostIts) {
      if (postIt.equals(postItInQuestion)) {
        isPostItInAllPostIts = true;
        break;
      }
    }
    return isPostItInAllPostIts;
  }


}
