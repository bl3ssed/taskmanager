package com.example.taskmanager.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionEventLogger implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventLogger.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof HttpSessionCreatedEvent) {
            HttpSessionCreatedEvent sessionCreatedEvent = (HttpSessionCreatedEvent) event;
            logger.info("Сессия создана: ID={}", sessionCreatedEvent.getSession().getId());
        } else if (event instanceof HttpSessionDestroyedEvent) {
            HttpSessionDestroyedEvent sessionDestroyedEvent = (HttpSessionDestroyedEvent) event;
            logger.info("Сессия завершена: ID={}", sessionDestroyedEvent.getId());
        }
    }
}
