package ru.lytvest.auth.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.lytvest.auth.config.AppStartup;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String queryString = request.getQueryString();
        if (request.getRequestURI().contains("/oauth2/authorize") && queryString != null) {
            log.info("=== OAUTH2 AUTHORIZE REQUEST ===");
            log.info("URL: " + request.getRequestURL());
            log.info("Query String: " + queryString);
            log.info("Method: " + request.getMethod());
            log.info("=== END OAUTH2 REQUEST ===");
        }
        
        filterChain.doFilter(request, response);
    }
}