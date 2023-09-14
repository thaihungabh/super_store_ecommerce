package com.shopee.clone.rest_controller.security;

import com.shopee.clone.rest_controller.security.jwt.JWTAuthenticationEntryPoint;
import com.shopee.clone.rest_controller.security.jwt.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    private final UserDetailServiceImpl userDetailsService;
    private final JWTAuthenticationEntryPoint unauthorizedHandler;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JWTFilter jwtFilter,
                          UserDetailServiceImpl userDetailsService,
                          JWTAuthenticationEntryPoint unauthorizedHandler,
                          LogoutHandler logoutHandler) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((auth) -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/test/**").permitAll();
                    auth.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider())
                .logout((logout) ->
                                logout.logoutUrl("/api/v1/auth/logout")
                                        .addLogoutHandler(logoutHandler)
                                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext())));

        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
}