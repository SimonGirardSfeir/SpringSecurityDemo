package org.girardsimon.springsecuritydemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${robot-authentication.passwords}")
    private List<String> passwords;

    // Unchecked cast is normal with the Spring Security's ObjectPostProcessor use
    @Bean
    @SuppressWarnings("unchecked")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RobotLoginConfigurer configurer = new RobotLoginConfigurer()
                .password(passwords);
        return http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/favicon.ico").permitAll();
                    auth.anyRequest().authenticated();
                })
                .formLogin(withDefaults())
                .oauth2Login(oauth2configurer -> oauth2configurer.withObjectPostProcessor(
                        new ObjectPostProcessor<AuthenticationProvider>() {
                            @Override
                            public <O extends AuthenticationProvider> O postProcess(O object) {
                                return (O) new RateLimitedAuthenticationProvider(object);
                            }
                        }))
                .with(configurer, withDefaults())
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
