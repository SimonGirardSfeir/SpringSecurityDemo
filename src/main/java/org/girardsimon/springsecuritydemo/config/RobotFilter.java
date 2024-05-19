package org.girardsimon.springsecuritydemo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class RobotFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "x-robot-password";

    private final AuthenticationManager authenticationManager;

    public RobotFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(shouldNotExecuteFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String password = request.getHeader(HEADER_NAME);
        RobotAuthentication authRequest = RobotAuthentication.unauthenticated(password);
        try {
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContext newContext = SecurityContextHolder.createEmptyContext();
            newContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(newContext);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type", "text/plain;charset=UTF-8");
            response.getWriter().println(e.getMessage());
        }
    }

    private static boolean shouldNotExecuteFilter(HttpServletRequest request) {
        return !Collections.list(request.getHeaderNames()).contains(HEADER_NAME);
    }
}
