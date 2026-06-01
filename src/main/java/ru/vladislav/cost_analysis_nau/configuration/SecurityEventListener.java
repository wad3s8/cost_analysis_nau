package ru.vladislav.cost_analysis_nau.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityEventListener.class);

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        log.info("User '{}' logged in", event.getAuthentication().getName());
    }

    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("Login failed for '{}': {}", event.getAuthentication().getName(),
                event.getException().getMessage());
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        log.info("User '{}' logged out", event.getAuthentication().getName());
    }
}
