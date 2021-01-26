package com.baumeredv.kanbanboard.model.util;


import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SessionFactory {

  private static org.hibernate.SessionFactory sessionFactory;
  private static ServiceRegistry serviceRegistry;

  private SessionFactory(){
  }

  public static org.hibernate.SessionFactory sessionFactory() {
    if (sessionFactory == null){
      Configuration configuration = new Configuration();
      configuration.configure();
      assert(serviceRegistry == null);
      serviceRegistry = new StandardServiceRegistryBuilder()
          .applySettings(configuration.getProperties())
          .build();
      sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }
    return sessionFactory;
  }

}
