package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.Group;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupDAO {
    private DAO<Group> dao;

    @Autowired
    public GroupDAO(SessionFactory sessionFactory) {
        this.dao = new DAO<>(sessionFactory, Group.class);
    }

    public List<Group> getAll() throws DAOException {
        return dao.getAll();
    }

    public void save(Group group) throws DAOException {
        dao.save(group);
    }

    public void delete(Group group) throws DAOException {
        dao.delete(group);
    }

    public void deleteById(long id) throws DAOException {
        dao.deleteById(id);
    }
}
