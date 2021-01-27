package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.dto.PostItDTO;
import com.baumeredv.kanbanboard.model.dto.PostItId;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.util.ArrayList;
import java.util.List;
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
    for(PostItDTO postItDTO : allQuery.getResultList()){
      PostIt postIt = new PostIt(postItDTO.getPostItId().getText(), PostItStage.valueOf(postItDTO.getPostItId().getStage()));
      postIts.add(postIt);
    }
    return postIts;
  }

  @Override
  public void deletePostIt(PostIt postIt) {

  }

  @Override
  public PostIt changePostItStage(PostIt postIt, PostItStage newStage)
      throws ThereIsNoSuchPostItException {
    return null;
  }

  @Override
  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    return null;
  }
}
