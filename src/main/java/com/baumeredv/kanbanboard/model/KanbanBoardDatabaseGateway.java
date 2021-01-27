package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.dto.PostItDTO;
import com.baumeredv.kanbanboard.model.dto.PostItId;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Component("KanbanBoardDatabaseGateway")
public class KanbanBoardDatabaseGateway implements KanbanBoardGateway {

  private final SessionFactory sessionFactory;

  public KanbanBoardDatabaseGateway() {
    sessionFactory = com.baumeredv.kanbanboard.model.util.SessionFactory.sessionFactory();
  }

  @Override
  public PostIt addPostIt(String text, PostItStage stage) {
    PostItId postItId = new PostItId(text, stage.name());
    PostItDTO postItDTO = new PostItDTO(postItId);

    Session session = sessionFactory.openSession();
    session.beginTransaction();
    try {
      session.save(postItDTO);
      session.getTransaction().commit();
      session.close();
      return new PostIt(text, stage); //REVIEW: should I assert that it's in the database first?
    } catch (Exception e) {
      session.getTransaction().rollback();
      session.close();
      throw e;
      //throw new PersistenceException("An unknown error occurred when connecting to the database");
    }
    //REVIEW: I believe error handling belongs in a different class, probably the Gateway?
  }

  @Override
  public Iterable<PostIt> allPostIts() {
    Session session = sessionFactory.openSession();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<PostItDTO> criteriaQuery = criteriaBuilder.createQuery(PostItDTO.class);
    Root<PostItDTO> rootEntry = criteriaQuery.from(PostItDTO.class);
    CriteriaQuery<PostItDTO> all = criteriaQuery.select(rootEntry);

    TypedQuery<PostItDTO> allQuery = session.createQuery(all);
    List<PostIt> postIts = new ArrayList<>();
    for (PostItDTO postItDTO : allQuery.getResultList()) {
      PostIt postIt = new PostIt(postItDTO.getPostItId().getText(),
          PostItStage.valueOf(postItDTO.getPostItId().getStage()));
      postIts.add(postIt);
    }
    session.close();
    return postIts;
  }

  @Override
  public void deletePostIt(PostIt postIt) {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    PostItDTO postItDTO = session
        .get(PostItDTO.class, new PostItId(postIt.text(), postIt.stage().name()));
    if (postItDTO == null) {
      throw new ThereIsNoSuchPostItException("");
    }
    session.delete(postItDTO);
    session.getTransaction().commit();
    session.close();
  }

  @Override
  public PostIt changePostItStage(PostIt postIt, PostItStage newStage)
      throws ThereIsNoSuchPostItException {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    System.out.println("---getting DTO---");
    PostItDTO postItDTO = session.get(PostItDTO.class,
        new PostItId(postIt.text(), postIt.stage().name()));
    if (postItDTO == null){
      throw new ThereIsNoSuchPostItException("");
    }
    /*System.out.println("---evicting DTO---");
    session.evict(postItDTO);
    System.out.println("---setting new post it id for DTO---");
    postItDTO.setPostItId(new PostItId(postIt.text(), newStage.name()));
    System.out.println("---merging DTO---");
    postItDTO = (PostItDTO) session.merge(postItDTO);
    System.out.println("---committing transaction---");
    session.getTransaction().commit();
    System.out.println("---returning---");*/
    session.delete(postItDTO);
    session.save(new PostItDTO(new PostItId(postIt.text(), newStage.name())));
    postItDTO = session.get(PostItDTO.class, new PostItId(postIt.text(), newStage.name()));
    session.getTransaction().commit();
    session.close();
    return new PostIt(postItDTO.getPostItId().getText(), PostItStage.valueOf(postItDTO.getPostItId().getStage()));
    //TODO: use an actual primary key and refactor this to be an update instead of delete plus insert
  }

  @Override
  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    System.out.println("---getting DTO---");
    PostItDTO postItDTO = session.get(PostItDTO.class,
        new PostItId(postIt.text(), postIt.stage().name()));
    if (postItDTO == null){
      throw new ThereIsNoSuchPostItException("");
    }
    session.delete(postItDTO);
    session.save(new PostItDTO(new PostItId(newText, postIt.stage().name())));
    postItDTO = session.get(PostItDTO.class, new PostItId(newText, postIt.stage().name()));
    session.getTransaction().commit();
    session.close();
    return new PostIt(postItDTO.getPostItId().getText(), PostItStage.valueOf(postItDTO.getPostItId().getStage()));
    //TODO: clean this up just like you gotta clean up the stage changing method
  }
}
