package ru.lytvest.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PathPrefixFilter implements Filter {

    private static final String PREFIX = "/ya-auth";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String forwardedPrefix = httpRequest.getHeader("X-Forwarded-Prefix");
        
        if (forwardedPrefix != null && !forwardedPrefix.isEmpty()) {
            chain.doFilter(new PrefixedRequest(httpRequest, forwardedPrefix), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class PrefixedRequest extends HttpServletRequestWrapper {
        private final String prefix;

        public PrefixedRequest(HttpServletRequest request, String prefix) {
            super(request);
            this.prefix = prefix;
        }

        @Override
        public String getContextPath() {
            return prefix + super.getContextPath();
        }

        @Override
        public String getServletPath() {
            return "";
        }
    }
}