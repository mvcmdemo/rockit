package com.rs.mv.rockit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/")
public class MainController {

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
}
