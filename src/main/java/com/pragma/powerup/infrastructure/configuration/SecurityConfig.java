package com.pragma.powerup.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                // HU-5: Solo el ADMIN puede crear restaurantes
                .antMatchers(HttpMethod.POST, "/restaurant/").hasRole("ADMIN")
                // HU-5: Solo el PROPIETARIO puede crear/modificar platos
                .antMatchers(HttpMethod.POST, "/dish/").hasRole("PROPIETARIO")
                .antMatchers(HttpMethod.PUT, "/dish/**").hasRole("PROPIETARIO")
                .antMatchers(HttpMethod.GET, "/restaurant/").hasRole("CLIENTE")
                .antMatchers(HttpMethod.GET, "/dish/restaurant/**").hasRole("CLIENTE")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}