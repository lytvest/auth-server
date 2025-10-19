package ru.lytvest.auth.controller;

import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final AuthorizationServerSettings authorizationServerSettings;

    public HealthController(AuthorizationServerSettings authorizationServerSettings) {
        this.authorizationServerSettings = authorizationServerSettings;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Greenhouse OAuth Server");
        response.put("issuer", authorizationServerSettings.getIssuer());
        response.put("authorizationEndpoint", authorizationServerSettings.getAuthorizationEndpoint());
        response.put("tokenEndpoint", authorizationServerSettings.getTokenEndpoint());
        return response;
    }
}