package com.pragma.powerup.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() //  para que Postman pueda hacer POST
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // Permitimos por ahora para pruebas

        return http.build();
    }
}