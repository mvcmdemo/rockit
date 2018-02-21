package com.rs.mv.rockit.beans;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    SessionFactory getSessionFactory() throws ExceptionInInitializerError {
        SessionFactory sessionFactory;
        try {
            sessionFactory = new org.hibernate.cfg.Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
        return sessionFactory;
    }
}
