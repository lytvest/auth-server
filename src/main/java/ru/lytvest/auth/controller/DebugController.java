package ru.lytvest.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/debug-auth")
    public Map<String, String[]> debugAuth(HttpServletRequest request) {
        return request.getParameterMap();
    }
}