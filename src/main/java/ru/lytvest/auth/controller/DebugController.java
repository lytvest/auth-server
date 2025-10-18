package ru.lytvest.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/debug/oauth")
    public Map<String, Object> debugOAuth(HttpServletRequest request) {
        Map<String, Object> debugInfo = new HashMap<>();
        
        debugInfo.put("requestURI", request.getRequestURI());
        debugInfo.put("queryString", request.getQueryString());
        debugInfo.put("method", request.getMethod());
        
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        debugInfo.put("headers", headers);
        
        Map<String, String[]> parameters = request.getParameterMap();
        debugInfo.put("parameters", parameters);
        
        return debugInfo;
    }
}