package com.rs.mv.rockit;

import com.rs.mv.rockit.dao.MachineDAO;
import com.rs.mv.rockit.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MachineService {
    private static final int STATUS_WAIT_TIMEOUT = 1000;
    private static final int PORT_CHECK_TIMEOUT = 5000;
    private static final int STATUS_REFRESH_INTERVAL = 10000;

    private MachineDAO machineDAO;
    private List<Machine> machines;
    private Thread statesWorker;
    private Network network;

    @Autowired
    public MachineService(MachineDAO machineDAO, Network network) throws DAOException {
        this.machineDAO = machineDAO;
        this.network = network;
        refreshMachines();
        startStatesWorker();
    }

    public List<Machine> getAll() {
        return machines;
    }

    public Machine getById(long id) throws DAOException {
        for (Machine machine : machines) {
            if (machine.getId() == id) {
                return machine;
            }
        }
        return machineDAO.getById(id);
    }

    public void save(Machine machine) throws DAOException {
        machineDAO.save(machine);
        refreshMachines();
    }

    public void deleteById(long id) throws DAOException {
        machineDAO.deleteById(id);
        refreshMachines();
    }

    private void refreshMachines() throws DAOException {
        List<Machine> loadedMachines = machineDAO.getAll();
        if (this.machines != null) {
            for (Machine cachedMachine : this.machines) {
                for (Machine loadedMachine : loadedMachines) {
                    if (loadedMachine.getId() == cachedMachine.getId()) {
                        loadedMachine.setState(cachedMachine.getState());
                        loadedMachine.setPlatform(cachedMachine.getPlatform());
                    }
                }
            }
        }
        this.machines = loadedMachines;
    }

    private void startStatesWorker() {
        if (statesWorker == null) {
            statesWorker = new Thread(this::getStates);
            statesWorker.start();
        }
    }

    private void getStates() {
        while (true) {
            if (machines != null) {
                for (Machine machine : machines) {
                    machine.setState(getState(machine));
                }
                try {
                    Thread.sleep(STATUS_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private MachineStates getState(Machine machine) {
        boolean isReachable;
        try {
            isReachable = network.pingHost(machine.getHost(), STATUS_WAIT_TIMEOUT);
        } catch (Exception e) {
            return MachineStates.OFFLINE;
        }
        return isReachable ? MachineStates.ONLINE : MachineStates.OFFLINE;
    }

    public Map<Long, MachineStates> getMachineStates() {
        Map<Long, MachineStates> states = new HashMap<>();
        for (Machine machine : machines) {
            states.put(machine.getId(), machine.getState());
        }
        return states;
    }

    public Map<Long, User> getMachineUsers() {
        Map<Long, User> users = new HashMap<>();
        for (Machine machine : machines) {
            users.put(machine.getId(), machine.getUsedBy());
        }
        return users;
    }

    public void grab(long machineId) throws Exception {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Unable to get current user");
        }
        Machine machine = getById(machineId);
        User usedBy = machine.getUsedBy();
        if (usedBy != null && usedBy.getId() != currentUser.getId()) {
            throw new Exception("Machine is already used");
        }
        machine.setUsedBy(currentUser);
        machineDAO.save(machine);
    }

    public void release(long machineId) throws Exception {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Unable to get current user");
        }
        Machine machine = getById(machineId);
        User usedBy = machine.getUsedBy();
        if (usedBy == null) {
            throw new Exception("Machine is not used");
        }
        if (usedBy.getId() != currentUser.getId()) {
            throw new Exception("You are not using this machine");
        }
        machine.setUsedBy(null);
        machineDAO.save(machine);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().size() > 0) {
            GrantedAuthority authority = authentication.getAuthorities().iterator().next();
            if (authority instanceof User) {
                return (User)authority;
            }
        }
        return null;
    }
}
