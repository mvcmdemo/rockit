package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.User;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDAO {
    private DAO<User> dao;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        dao = new DAO<>(sessionFactory, User.class);
    }

    public List<User> getAll() throws DAOException {
        return dao.getAll();
    }

    public void save(User user) throws DAOException {
        dao.save(user);
    }

    public void delete(User user) throws DAOException {
        dao.delete(user);
    }

    public void deleteById(long id) throws DAOException {
        dao.deleteById(id);
    }
}
