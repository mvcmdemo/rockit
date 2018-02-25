package com.rs.mv.rockit.beans;

import com.jcraft.jsch.JSch;
import com.rs.mv.rockit.Group;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    SessionFactory getSessionFactory() throws ExceptionInInitializerError {
        SessionFactory sessionFactory;
        try {
            sessionFactory = new org.hibernate.cfg.Configuration().configure().addAnnotatedClass(Group.class).buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
        return sessionFactory;
    }

    @Bean
    JSch getJSch() {
        return new JSch();
    }
}
