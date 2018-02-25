package com.rs.mv.rockit;

import com.rs.mv.rockit.dao.MachineDAO;
import com.rs.mv.rockit.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
                    if (machine.getPlatform() == MachinePlatforms.UNKNOWN && machine.getState() == MachineStates.ONLINE) {
                        machine.setPlatform(getPlatform(machine));
                    }
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

    private MachinePlatforms getPlatform(Machine machine) {
        return network.getHostPlatform(machine.getHost(), PORT_CHECK_TIMEOUT);
    }
}
