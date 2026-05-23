package com.attendance.config;

import com.attendance.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security configuration.
 * Defines which URLs require which roles, login/logout settings, etc.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize annotations in controllers
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * BCrypt password encoder bean.
     * Used to hash passwords before storing and to verify during login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Main security filter chain.
     * Defines URL access rules, login/logout behavior.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // --- URL Authorization Rules ---
            .authorizeHttpRequests(auth -> auth
                // Public URLs (no login needed)
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                // Admin-only URLs
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Teacher-only URLs
                .requestMatchers("/teacher/**").hasRole("TEACHER")

                // Student-only URLs
                .requestMatchers("/student/**").hasRole("STUDENT")

                // All other URLs need authentication
                .anyRequest().authenticated()
            )

            // --- Login Configuration ---
            .formLogin(login -> login
                .loginPage("/login")               // Custom login page URL
                .loginProcessingUrl("/login")       // Form action URL (POST)
                .usernameParameter("username")
                .passwordParameter("password")
                // After login, redirect based on role (handled by successHandler)
                .successHandler(new CustomAuthSuccessHandler())
                .failureUrl("/login?error=true")   // Redirect on wrong credentials
                .permitAll()
            )

            // --- Logout Configuration ---
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Disable CSRF for simplicity (enable in production with proper token handling)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
