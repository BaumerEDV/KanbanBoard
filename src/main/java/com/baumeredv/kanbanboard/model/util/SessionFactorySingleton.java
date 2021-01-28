package com.baumeredv.kanbanboard.model.util;


import com.baumeredv.kanbanboard.model.dto.PostItDTO;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Component;

@Component("SessionFactorySingleton")
public class SessionFactorySingleton {

  private org.hibernate.SessionFactory sessionFactory;
  private ServiceRegistry serviceRegistry;

  private SessionFactorySingleton() {
  }

  public org.hibernate.SessionFactory sessionFactory() {
    if (sessionFactory == null) {
      Configuration configuration = new Configuration();
      configuration.configure("/hibernate.cfg.xml");
      configuration.addAnnotatedClass(PostItDTO.class);
      assert (serviceRegistry == null);
      serviceRegistry = new StandardServiceRegistryBuilder()
          .applySettings(configuration.getProperties())
          .build();
      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    return sessionFactory;
  }

}
