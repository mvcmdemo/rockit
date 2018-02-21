package com.rs.mv.rockit.dao;

import com.rs.mv.rockit.Machine;
import com.rs.mv.rockit.exception.DAOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineDAO {
    private SessionFactory sessionFactory;

    @Autowired
    public MachineDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Machine> getAll() throws DAOException {
        List<Machine> machines;
        try (Session session = sessionFactory.openSession()) {
            machines = session.createQuery("from Machine ", Machine.class).list();
        } catch (Exception e) {
            throw new DAOException("Error listing machines", e);
        }
        return machines;
    }

    public void save(Machine machine) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(machine);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException("Error saving machine", e);
        }
    }

    public void delete(Machine machine) throws DAOException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(machine);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new DAOException("Error deleting machine", e);
        }
    }
}
