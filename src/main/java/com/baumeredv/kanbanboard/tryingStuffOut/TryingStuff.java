package com.baumeredv.kanbanboard.tryingStuffOut;

import com.baumeredv.kanbanboard.model.dto.PostIt;
import com.baumeredv.kanbanboard.model.dto.PostItId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TryingStuff {

  public static void main(String[] args){
    PostIt postIt = new PostIt(new PostItId("my first postit", "BACKLOG"));

    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save(postIt);
    session.getTransaction().commit();
    session.close();
    //error: gettransaction . rollback

  }

}
