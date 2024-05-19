package org.girardsimon.springsecuritydemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${robot-authentication.passwords}")
    private List<String> passwords;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DefaultAuthenticationEventPublisher authenticationEventPublisher) throws Exception {
        var authManager = new ProviderManager(new RobotAuthenticationProvider(passwords));
        authManager.setAuthenticationEventPublisher(authenticationEventPublisher);
        return http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/favicon.ico").permitAll();
                    auth.anyRequest().authenticated();
                })
                .formLogin(withDefaults())
                .oauth2Login(withDefaults())
                .addFilterBefore(new RobotFilter(authManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new DumbAuthenticationProvider())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("user")
                        .password("{noop}password")
                        .authorities("ROLE_user")
                        .build());
    }
}
