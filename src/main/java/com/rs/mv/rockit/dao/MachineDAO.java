package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.Group;
import com.rs.mv.rockit.Machine;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineDAO {
    private DAO<Machine> dao;

    @Autowired
    public MachineDAO(SessionFactory sessionFactory) {
        this.dao = new DAO<>(sessionFactory, Machine.class);
    }

    public List<Machine> getAll() throws DAOException {
        return dao.getAll();
    }

    public Machine getById(long id) throws DAOException {
        return dao.getById(id);
    }

    public void save(Machine machine) throws DAOException {
        dao.save(machine);
    }

    public void delete(Machine machine) throws DAOException {
        dao.delete(machine);
    }

    public void deleteById(long id) throws DAOException {
        dao.deleteById(id);
    }

}
