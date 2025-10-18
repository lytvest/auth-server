package ru.lytvest.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuth2AuthorizationServerConfig {
    // Получаем адрес сервера из настроек
    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${server.port:8080}") // Убедитесь, что порт совпадает с application.properties
    private String serverPort;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Применяем стандартную безопасность OAuth2 Authorization Server
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Указываем, что эта цепочка применяется ТОЛЬКО к OAuth2-эндпоинтам
        http.securityMatcher("/oauth2/**");

        return http.build();
    }

    // --- Цепочка для кастомной страницы логина и других публичных URL ---
    @Bean
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login") // Применяем только к /login
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Разрешаем доступ к /login
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Указываем кастомную страницу логина
                        .permitAll() // Разрешаем всем доступ к странице логина
                );

        return http.build();
    }

    // --- Цепочка для защиты всех остальных API ---
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // Применить к любому пути, НЕ перехваченному предыдущими цепочками
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // Требовать аутентификацию для всех запросов, не перехваченных предыдущими цепочками
                )
                // Настраиваем OAuth2 Resource Server для проверки JWT
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt); // Используем JWT для аутентификации API

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId("client-id")
                .clientId("my-client")
                .clientSecret(passwordEncoder().encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8000/login/") // Обратите внимание на localhost:8080
                .scope("read")
                .scope("write")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        // Установите issuer URI ОБЯЗАТЕЛЬНО, чтобы Spring Security знал адрес сервера
        String issuerUri = "http://" + serverAddress + ":" + serverPort;
        System.out.println("Setting issuer URI for Authorization Server: " + issuerUri); // Лог для отладки
        return AuthorizationServerSettings.builder()
                .issuer(issuerUri) // ВАЖНО: устанавливаем issuer URI
                .build();
    }

}