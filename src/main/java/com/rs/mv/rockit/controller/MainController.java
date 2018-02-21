package com.rs.mv.rockit.controller;

import com.rs.mv.rockit.Machine;
import com.rs.mv.rockit.dao.GroupDAO;
import com.rs.mv.rockit.dao.MachineDAO;
import com.rs.mv.rockit.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Controller
public class MainController {
    private GroupDAO groupDAO;
    private MachineDAO machineDAO;

    @Autowired
    public MainController(GroupDAO groupDAO, MachineDAO machineDAO) {
        this.groupDAO = groupDAO;
        this.machineDAO = machineDAO;
    }

    @RequestMapping(value = "/healthcheck", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> healthCheck() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        resp.put("status", "ok");
        resp.put("time", LocalDateTime.now());
        resp.put("OS", System.getProperty("os.name"));
        response = ResponseEntity.ok(resp);
        return response;
    }

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(value = "/machines", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> getMachines() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("machines", machineDAO.getAll());
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.ok(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> saveMachine(@RequestBody Machine machine) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineDAO.save(machine);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.ok(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines/{id}", method = {RequestMethod.DELETE, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> saveMachine(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineDAO.deleteById(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.ok(resp);
        }
        return response;
    }

    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> test() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("error", e.getMessage());
            response = ResponseEntity.ok(resp);
        }
        return response;
    }
}
