package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.Group;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public GroupDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Group> getAll() throws DAOException {
        List<Group> groups;
        try (Session session = sessionFactory.openSession()) {
            groups = session.createQuery("from Group ", Group.class).list();
        } catch (Exception e) {
            throw new DAOException("Error listing groups", e);
        }
        return groups;
    }

    public void save(Group group) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException("Error saving group", e);
        }
    }
}
