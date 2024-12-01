package com.example.taskmanager.config;

import com.example.taskmanager.repositories.UserRepository;
import com.example.taskmanager.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import jakarta.servlet.http.HttpServletRequest; // Используем Jakarta API
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository; // Внедрение репозитория через конструктор
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) -> {
            logger.info("=== УСПЕШНАЯ АУТЕНТИФИКАЦИЯ ===");
            logger.info("Пользователь: {}", authentication.getName());
            logger.info("IP адрес клиента: {}", request.getRemoteAddr());
            logger.info("Роли пользователя: {}", authentication.getAuthorities());
            logger.info("Путь запроса: {}", request.getRequestURI());

            // Пример добавления данных пользователя в сессию
            request.getSession().setAttribute("username", authentication.getName());

            // Перенаправление на домашнюю страницу
            response.sendRedirect("/home");
        };
    }
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) -> {
            logger.error("=== НЕУДАЧНАЯ АУТЕНТИФИКАЦИЯ ===");
            logger.error("Причина ошибки: {}", exception.getMessage());
            logger.error("IP адрес клиента: {}", request.getRemoteAddr());
            logger.error("Путь запроса: {}", request.getRequestURI());

            // Перенаправление на страницу логина с параметром ошибки
            response.sendRedirect("/login?error=true");
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home","/profile","/tasks/user/**").authenticated() // Защищенный маршрут
                        .requestMatchers("/login", "/register", "/resources/**").permitAll() // Разрешенные маршруты
                )
                .formLogin(login -> login
                        .loginPage("/login") // Настройка страницы входа
                        .defaultSuccessUrl("/home", true)
                        .successHandler(authenticationSuccessHandler()) // Обработчик успешной аутентификации
                        .failureHandler(authenticationFailureHandler()) // Обработчик ошибки аутентификации
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/register")  // Перенаправление на страницу login, если сессия невалидна
                        .maximumSessions(1)  // Ограничение на одну сессию на пользователя
                        .expiredUrl("/login?expired-session=true")  // Перенаправление на страницу login, если сессия истекла
                )
                .csrf(csrf -> csrf.disable());
        // Отключаем CSRF для тестирования

        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

}
