package com.example.demo.config;

import java.util.Arrays;
import java.util.Collections;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class AppConfig {

    private final JwtValidator jwtValidator;

    public AppConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtValidator jwtValidator
    ) throws Exception {

        http
        	
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // PUBLIC
                .requestMatchers("/auth/**").permitAll()

                // ADMIN ONLY
                .requestMatchers(HttpMethod.POST, "/api/products/**")
                    .hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/products/**")
                    .hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                    .hasAuthority("ADMIN")

                // USER + ADMIN
                .requestMatchers(HttpMethod.GET, "/api/products/**")
                    .permitAll()
                    .requestMatchers("/api/payment/**", "/api/payments/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtValidator,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration cfg = new CorsConfiguration();

                cfg.setAllowedOrigins(Arrays.asList(
                        "http://localhost:3000",
                        "http://localhost:4200",
                        "http://localhost:5173",
                        "https://shopyverse.vercel.app"
                ));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                cfg.setAllowCredentials(true);
                cfg.setMaxAge(3600L);

                return cfg;
            }
        };
    }
}