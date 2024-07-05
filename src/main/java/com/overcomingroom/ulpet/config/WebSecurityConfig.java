package com.overcomingroom.ulpet.config;

import com.overcomingroom.ulpet.auth.config.JwtAuthenticationFilter;
import com.overcomingroom.ulpet.auth.config.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(CsrfConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(HttpBasicConfigurer::disable)
        .authorizeHttpRequests(
            requests ->
                requests
                  .requestMatchers(
                      "/v1/users/login",
                      "/v1/users/signup",
                      "/v1/users/password",
                      "/v1/users/check-email",
                      "/swagger-ui/**",
                      "/swagger-resources/**",
                      "/v3/api-docs/**"
                  )
                  .permitAll()
                  .anyRequest()
                  .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass());

    return http.build();
  }

}
