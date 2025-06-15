package com.example.cms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth
            // swagger UI and docs
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**")
            .permitAll()

            // public read-only for articles & images
            .requestMatchers(HttpMethod.GET, "/api/articles/**", "/api/images/**")
            .permitAll()

            // auth endpoints are open (login/signup)
            .requestMatchers("/api/auth/**")
            .permitAll()

            // any write operation on articles/images requires ADMIN
            .requestMatchers(
                HttpMethod.POST, "/api/articles/**", "/api/images/**")
            .hasRole("ADMIN")
            .requestMatchers(
                HttpMethod.PUT, "/api/articles/**", "/api/images/**")
            .hasRole("ADMIN")
            .requestMatchers(
                HttpMethod.DELETE, "/api/articles/**", "/api/images/**")
            .hasRole("ADMIN")

            // everything else needs a valid token
            .anyRequest().authenticated())

        // hook in your JWT filter
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

        // no default login form
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
