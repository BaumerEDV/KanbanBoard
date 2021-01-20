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

  //TODO: refactor names and nesting structure

  @BeforeEach
  public void createModel() {
    ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
    this.model = context.getBean(KanbanBoardModel.class);
  }

  @Nested
  class WhenAddingPostIts {

    @Nested
    class AnAddedPostIt {

      private final String ADDED_POST_IT_TEXT = "a test post it";
      private PostIt addedPostIt;

      @BeforeEach
      public void setup() {
        addedPostIt = model.addPostIt(ADDED_POST_IT_TEXT);
      }

      @Test
      public void isAddedToTheModel() {
        assertTrue(isPostItInModel(addedPostIt));
      }

      @Test
      public void hasTheTextItWasGiven() {
        assertEquals(ADDED_POST_IT_TEXT, addedPostIt.text());
      }

      @Test
      public void isInTheBacklogStage() {
        assertEquals(PostItStage.BACKLOG, addedPostIt.stage());
      }

      @Test
      public void canBeAddedAlongsideASecondOne() {
        final String SECOND_POST_IT_TEXT = "second post it text";
        PostIt secondPostIt = model.addPostIt(SECOND_POST_IT_TEXT);

        assertTrue(isPostItInModel(addedPostIt));
        assertTrue(isPostItInModel(secondPostIt));
      }

    }

    @Nested
    class APostItCannotBeAdded {

      @Test
      public void ifItIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> model.addPostIt(""));
      }

      @Test
      public void ifItIsNull() {
        assertThrows(NullPointerException.class, () -> model.addPostIt(null));
      }
    }
  }

  @Nested
  class WhenDeletingPostIts {

    private final String POST_IT_TO_BE_DELETED_TEXT = "text of the post it to be deleted";

    @Nested
    class AfterAPostItWasAdded {

      private PostIt postIt;

      @BeforeEach
      public void setup() {
        postIt = model.addPostIt(POST_IT_TO_BE_DELETED_TEXT);
      }

      @Test
      public void deletingThePostItRemovesItFromTheModel() {
        model.deletePostIt(postIt);
        assertFalse(isPostItInModel(postIt));
      }
    }

    @Test
    public void deletingANonexistentPostItThrowsException() throws Exception {
      PostIt postIt = createPostItInstance(POST_IT_TO_BE_DELETED_TEXT);
      assertThrows(NoSuchElementException.class, () -> model.deletePostIt(postIt));
    }

    @Test
    public void passingNullToDeleteThrowsException() {
      assertThrows(NullPointerException.class, () -> model.deletePostIt(null));
    }


  }

  @Nested
  class WhenMovingPostIts {

    private final String POST_IT_TEXT = "some text";
    private ArrayList<PostIt> temporaryPostIts;

    @BeforeEach
    public void setup() {
      temporaryPostIts = new ArrayList<>();
    }

    @Nested
    class MovingNewPostItsForward {

      private PostIt postIt;
      private PostIt movedPostIt;

      @BeforeEach
      public void setup() throws Exception {
        postIt = model.addPostIt(POST_IT_TEXT);
      }

      @Test
      public void onceMovesToWIP() {
        movedPostIt = model.movePostItToNext(postIt);
        temporaryPostIts.add(postIt);
        assertEquals(PostItStage.WIP, movedPostIt.stage());
      }

      @Test
      public void twiceMovesToTEST() {
        movedPostIt = postIt;
        for (int i = 0; i < 2; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToNext(movedPostIt);
        }
        assertEquals(PostItStage.TEST, movedPostIt.stage());
      }

      @Test
      public void threeTimesMovesToDONE() {
        movedPostIt = postIt;
        for (int i = 0; i < 3; i++) {
          temporaryPostIts.add(movedPostIt);
          movedPostIt = model.movePostItToNext(movedPostIt);
        }
        assertEquals(PostItStage.DONE, movedPostIt.stage());
      }

      @Test
      public void fourTimesThrowsException() {
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
    class MovingDonePostItsBackward {

      private PostIt postIt;
      private PostIt movedPostIt;

      @BeforeEach
      public void setupADonePostIt() {
        postIt = model.addPostIt(POST_IT_TEXT);
        for (int i = 0; i < 3; i++) {
          postIt = model.movePostItToNext(postIt);
        }
        assertEquals(PostItStage.DONE, postIt.stage());
        //REVIEW: should I create such a post it via reflection instead of using methods?
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
    public void movingNonexistentPostItForwardThrowsForAllStages() throws Exception {
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
    public void movingNonexistentPostItBackwardsThrowsForAllStages() throws Exception {
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
  class WhenChangingPostItText {

    private final String NEW_TEXT = "new text";

    @Nested
    class OfAnExistingPostIt {

      private PostIt oldPostIt;
      private PostIt newPostIt;
      private final String OLD_TEXT = "old text";

      @BeforeEach
      public void setup() {
        oldPostIt = model.addPostIt(OLD_TEXT);
        newPostIt = model.changePostItText(oldPostIt, NEW_TEXT);
      }

      @Test
      public void theNewTextIsSavedInThePostIt() {
        assertEquals(NEW_TEXT, newPostIt.text());
      }

      @Test
      public void theChangedPostItIsInTheModel() {
        assertTrue(isPostItInModel(newPostIt));
      }

      @Test
      public void theOriginalPostItIsNotInTheModel() {
        assertFalse(isPostItInModel(oldPostIt));
      }
    }

    @Test
    public void changingTextOfANonexistentPostItThrows() throws Exception {
      PostIt nonexistentPostIt = createPostItInstance(NEW_TEXT);
      assertThrows(ThereIsNoSuchPostItException.class,
          () -> model.changePostItText(nonexistentPostIt, NEW_TEXT + "2"));
    //REVIEW: should this have its own nested class for a single test case?
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
