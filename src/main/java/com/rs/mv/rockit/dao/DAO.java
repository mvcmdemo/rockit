package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class DAO<T> {
    private SessionFactory sessionFactory;
    private Class clazz;

    public DAO(SessionFactory sessionFactory, Class clazz) {
        this.sessionFactory = sessionFactory;
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() throws DAOException {
        List<T> objects;
        try (Session session = sessionFactory.openSession()) {
            objects = session.createQuery("from " + clazz.getSimpleName(), clazz).list();
        } catch (Exception e) {
            throw new DAOException(String.format("Error listing objects of class %s", clazz.getSimpleName()), e);
        }
        return objects;
    }

    @SuppressWarnings("unchecked")
    public T getById(long id) throws DAOException {
        T object;
        try (Session session = sessionFactory.openSession()) {
            object = (T)session.get(clazz, id);
        } catch (Exception e) {
            throw new DAOException(String.format("Error loading object of class %s", clazz.getSimpleName()), e);
        }
        return object;
    }

    public void save(T object) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException(String.format("Error saving object of class %s", clazz.getSimpleName()), e);
        }
    }

    public void delete(T object) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException(String.format("Error deleting object of class %s", clazz.getSimpleName()), e);
        }
    }

    public void deleteById(long id) throws DAOException {
        T object = getById(id);
        delete(object);
    }
}
