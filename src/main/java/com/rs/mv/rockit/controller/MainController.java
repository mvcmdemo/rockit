package com.rs.mv.rockit.controller;

import com.rs.mv.rockit.*;
import com.rs.mv.rockit.dao.GroupDAO;
import com.rs.mv.rockit.dao.UserDAO;
import com.rs.mv.rockit.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Controller
public class MainController {
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private MachineService machineService;
    private RDPGenerator rdpGenerator;

    @Autowired
    public MainController(GroupDAO groupDAO, UserDAO userDAO, MachineService machineService, RDPGenerator rdpGenerator) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.machineService = machineService;
        this.rdpGenerator = rdpGenerator;
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

    @RequestMapping("/terminal/{id}")
    public String terminal(Model model, @PathVariable long id) throws Exception {
        Machine machine = machineService.getById(id);
        if (machine != null) {
            model.addAttribute("machineID", id);
            model.addAttribute("machineHost", machine.getHost());
        } else {
            throw new Exception("Unable to find machine with specified ID");
        }
        return "terminal";
    }

    @RequestMapping("/filetree/{id}")
    public String fileTree(Model model, @PathVariable long id) throws Exception {
        return "filetree";
    }

    @RequestMapping(value = "/machines", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> getMachines() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("machines", machineService.getAll());
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("error", e.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machine_states", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> getMachineStates() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("states", machineService.getMachineStates());
            resp.put("machine_users", machineService.getMachineUsers());
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("error", e.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<ModelMap> saveMachine(@RequestBody Machine machine) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineService.save(machine);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<ModelMap> saveMachine(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineService.deleteById(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines/grab/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<ModelMap> grabMachine(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineService.grab(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/machines/release/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<ModelMap> releaseMachine(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            machineService.release(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/rdp/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public void getRDP(@PathVariable("id") long id, HttpServletResponse response) {
        try {
            Machine machine = machineService.getById(id);
            rdpGenerator.setMachine(machine);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + machine.getHost() + ".rdp");
            response.getOutputStream().print(rdpGenerator.getRDP());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/groups", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> getGroups() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("groups", groupDAO.getAll());
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("error", e.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/groups", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<ModelMap> saveGroup(@RequestBody Group group) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            groupDAO.save(group);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/groups/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<ModelMap> deleteMachine(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            groupDAO.deleteById(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/users", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ModelMap> getUsers() {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            resp.put("users", userDAO.getAll());
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("status", "error");
            resp.put("error", e.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/users", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<ModelMap> saveUser(@RequestBody User user) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            userDAO.save(user);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }

    @RequestMapping(value = "/users/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<ModelMap> deleteUser(@PathVariable("id") long id) {
        ModelMap resp = new ModelMap();
        ResponseEntity<ModelMap> response;
        try {
            userDAO.deleteById(id);
            resp.put("status", "ok");
            response = ResponseEntity.ok(resp);
        } catch (DAOException daoe) {
            resp.put("status", "error");
            resp.put("error", daoe.getMessage());
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
        return response;
    }
}
