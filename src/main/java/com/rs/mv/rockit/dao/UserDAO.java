package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.User;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<User> getAll() throws DAOException {
        List<User> users;
        try (Session session = sessionFactory.openSession()) {
            users = session.createQuery("from User ", User.class).list();
        } catch (Exception e) {
            throw new DAOException("Error listing users", e);
        }
        return users;
    }

    public void save(User user) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException("Error saving user", e);
        }
    }

    public void delete(User user) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException("Error deleting user", e);
        }
    }
}
