package com.todo.todoApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/login",
                                "/register",
                                "/login-form",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // API endpoints → JWT
                        .requestMatchers("/auth/**","/tasks/api/**").permitAll()

                        // ADMIN UI
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // MANAGER UI
                        .requestMatchers("/manager/**")
                        .hasAnyRole("MANAGER","ADMIN")

                        // USER UI
                        .requestMatchers("/tasks","/tasks/**")
                        .hasAnyRole("USER","MANAGER","ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}