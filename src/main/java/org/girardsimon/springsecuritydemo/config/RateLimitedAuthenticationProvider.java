package org.girardsimon.springsecuritydemo.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitedAuthenticationProvider implements AuthenticationProvider {
    private final AuthenticationProvider delegate;
    private final Map<String, Instant> cache = new ConcurrentHashMap<>();

    public RateLimitedAuthenticationProvider(AuthenticationProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication parentAuth = delegate.authenticate(authentication);
        if (parentAuth == null) {
            return null;
        }
        if(updateCache(parentAuth)) {
            return parentAuth;
        }
        throw new BadCredentialsException("🤠 Not so fast");
    }

    private boolean updateCache(Authentication parentAuth) {
        Instant now = Instant.now();
        Instant previousAuthTime = cache.get(parentAuth.getName());
        cache.put(parentAuth.getName(), now);
        return previousAuthTime == null || previousAuthTime.plus(Duration.ofMinutes(1L)).isBefore(now);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return delegate.supports(authentication);
    }
}
