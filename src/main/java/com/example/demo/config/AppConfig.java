package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    private final JwtValidator jwtValidator;

    public AppConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // ✅ Public routes
                .requestMatchers("/auth/**").permitAll()

                // ✅ Public GET product routes (search, browse, view)
                .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/category/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                // ✅ Admin routes
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // ✅ All other API routes require login
                .requestMatchers("/api/**").authenticated()

                .anyRequest().permitAll()
            )

            .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}