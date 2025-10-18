package ru.lytvest.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class AuthSuccessConfig {



    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                              HttpServletResponse response,
                                              org.springframework.security.core.Authentication authentication) 
                    throws IOException, ServletException {
                
                // Получаем исходный URL авторизации из сессии
                String savedRequest = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                if (savedRequest != null && savedRequest.contains("/oauth2/authorize")) {
                    // Перенаправляем обратно в OAuth2 flow
                    response.sendRedirect(savedRequest);
                } else {
                    // Иначе на главную
                    response.sendRedirect("/");
                }
            }
        };
    }
}