package ru.lytvest.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class AppStartup implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(AppStartup.class);
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== OAuth2 Server Started ===");
        log.info("Login URL: https://greenbots.ru/ya-auth/login");
        log.info("Auth URL: https://greenbots.ru/ya-auth/oauth2/authorize");
        log.info("Token URL: https://greenbots.ru/ya-auth/oauth2/token");
        log.info("Test users: user/password, admin/admin");
    }
}