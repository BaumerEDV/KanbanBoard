package com.baumeredv.kanbanboard.model;

import com.baumeredv.kanbanboard.model.dto.PostItDTO;
import com.baumeredv.kanbanboard.model.exceptions.ThereIsNoSuchPostItException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
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
    PostItDTO postItDTO = new PostItDTO(text, stage.name());

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
      PostIt postIt = new PostIt(postItDTO.getText(),
          PostItStage.valueOf(postItDTO.getStage()));
      postIts.add(postIt);
    }
    session.close();
    return postIts;
  }

  @Override
  public void deletePostIt(PostIt postIt) {
    Session session = sessionFactory.openSession();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<PostItDTO> criteriaQuery = criteriaBuilder.createQuery(PostItDTO.class);
    Root<PostItDTO> rootEntry = criteriaQuery.from(PostItDTO.class);
    Predicate[] predicates = new Predicate[2];
    predicates[0] = criteriaBuilder.equal(rootEntry.get("text"), postIt.text());
    predicates[1] = criteriaBuilder.equal(rootEntry.get("stage"), postIt.stage().name());
    CriteriaQuery<PostItDTO> deletionCriteriaQuery = criteriaQuery.select(rootEntry).where(predicates);
    TypedQuery<PostItDTO> deletionQuery = session.createQuery(deletionCriteriaQuery);
    List<PostItDTO> results = deletionQuery.getResultList();
    assert(results.size()<=1);
    if(results.isEmpty()){
      throw new ThereIsNoSuchPostItException("There was an attempt to delete a PostIt that doesn't exist");
    }
    PostItDTO postItToBeDeletedDTO = results.get(0);
    session.beginTransaction();
    session.delete(postItToBeDeletedDTO);
    session.getTransaction().commit();
    session.close();
  }

  @Override
  public PostIt changePostItStage(PostIt postIt, PostItStage newStage)
      throws ThereIsNoSuchPostItException {
    Session session = sessionFactory.openSession();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaUpdate<PostItDTO> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(PostItDTO.class);
    Root<PostItDTO> rootEntry = criteriaUpdate.from(PostItDTO.class);
    criteriaUpdate.set(rootEntry.get("stage"), newStage.name());
    Predicate[] predicates = new Predicate[2];
    predicates[0] = criteriaBuilder.equal(rootEntry.get("text"), postIt.text());
    predicates[1] = criteriaBuilder.equal(rootEntry.get("stage"), postIt.stage().name());
    criteriaUpdate.where(predicates);

    session.beginTransaction();
    int updatedEntitiesCount = session.createQuery(criteriaUpdate).executeUpdate();
    session.getTransaction().commit();
    assert(updatedEntitiesCount<=1);
    if(updatedEntitiesCount == 0){
      throw new ThereIsNoSuchPostItException("There was an attempt to change the state of a PostIt that doesn't exist");
    }
    return new PostIt(postIt.text(), newStage); //REVIEW: is it fine to create this here rather than pull it from the database?
  }

  @Override
  public PostIt changePostItText(PostIt postIt, String newText)
      throws ThereIsNoSuchPostItException {
    Session session = sessionFactory.openSession();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaUpdate<PostItDTO> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(PostItDTO.class);
    Root<PostItDTO> rootEntry = criteriaUpdate.from(PostItDTO.class);
    criteriaUpdate.set(rootEntry.get("text"), newText);
    Predicate[] predicates = new Predicate[2];
    predicates[0] = criteriaBuilder.equal(rootEntry.get("text"), postIt.text());
    predicates[1] = criteriaBuilder.equal(rootEntry.get("stage"), postIt.stage().name());
    criteriaUpdate.where(predicates);
    session.beginTransaction();
    int updatedEntitiesCount = session.createQuery(criteriaUpdate).executeUpdate();
    session.getTransaction().commit();
    assert(updatedEntitiesCount<=1);
    if(updatedEntitiesCount == 0){
      throw new ThereIsNoSuchPostItException("There was an attempt to change the text of a PostIt that doesn't exist");
    }
    return new PostIt(newText, postIt.stage()); //REVIEW: is it fine to create this here rather than pull it from the database?
  }
}
