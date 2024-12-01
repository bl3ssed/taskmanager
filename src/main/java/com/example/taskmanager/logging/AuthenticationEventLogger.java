package com.example.taskmanager.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventLogger implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEventLogger.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            // Событие успешной аутентификации
            logger.info("=== СОБЫТИЕ: УСПЕШНАЯ АУТЕНТИФИКАЦИЯ ===");
            logger.info("Пользователь: {}", ((AuthenticationSuccessEvent) event).getAuthentication().getName());
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            // Ошибка аутентификации: неверные учетные данные
            logger.error("=== СОБЫТИЕ: НЕУДАЧНАЯ АУТЕНТИФИКАЦИЯ ===");
            logger.error("Причина: Неверные учетные данные");
        } else if (event instanceof AuthenticationFailureDisabledEvent) {
            // Ошибка: учетная запись отключена
            logger.error("=== СОБЫТИЕ: АККАУНТ ОТКЛЮЧЕН ===");
            logger.error("Причина: Учетная запись отключена");
        }
    }
}
