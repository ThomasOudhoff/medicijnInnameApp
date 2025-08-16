package com.thomas.medicatieinnameapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF-beveiliging uitzetten (voor testen met Postman)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 👈 maakt alles publiek toegankelijk
                );
        return http.build();
    }
}
