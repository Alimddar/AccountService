package account.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationFailureHandler authenticationFailureHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, 
                                                    RestAuthenticationEntryPoint restAuthenticationEntryPoint) throws Exception {
        http
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        
                        // Admin endpoints - only ADMINISTRATOR role
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        
                        // Accountant endpoints - only ACCOUNTANT role
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
                        
                        // Employee payment endpoint - ACCOUNTANT or USER
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                        
                        // Security events endpoint - only AUDITOR role
                        .requestMatchers(HttpMethod.GET, "/api/security/events").hasRole("AUDITOR")
                        
                        // Auth changepass - any authenticated user
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        
                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )

                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}