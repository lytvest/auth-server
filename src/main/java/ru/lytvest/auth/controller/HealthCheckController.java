package ru.lytvest.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @RequestMapping(value = "/v1.0", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkAvailability() {
        return ResponseEntity.ok().build();
    }
}