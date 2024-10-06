package com.timekeeping.timekeeping.config;

import com.timekeeping.timekeeping.controllers.AuthenticationCookieFilter;
import com.timekeeping.timekeeping.services.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationCookieFilter authenticationCookieFilter;

    public SecurityConfig(AuthenticationCookieFilter authenticationCookieFilter) {
        this.authenticationCookieFilter = authenticationCookieFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .failureHandler(new CustomAuthenticationFailureHandler()) // Sử dụng failure handler tùy chỉnh
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/css/**", "/js/**", "/home", "/about", "/contact", "/blog").permitAll() // Allow access to specific paths without authentication
                        .anyRequest().permitAll() // Allow all other requests without authentication
                )
                .httpBasic(withDefaults()); // Use basic authentication as a default strategy for API-based authentication

        // Add custom filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationCookieFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
