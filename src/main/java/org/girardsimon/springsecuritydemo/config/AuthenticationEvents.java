package org.girardsimon.springsecuritydemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvents {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEvents.class);

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("SUCCESS 🦄 [{}] {}", event.getAuthentication().getClass().getName(),
                event.getAuthentication().getName());
    }
}
