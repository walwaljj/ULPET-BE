package com.overcomingroom.ulpet.auth.config;

import com.overcomingroom.ulpet.auth.service.JwtService;
import com.overcomingroom.ulpet.member.service.MemberService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final MemberService memberService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    final String BEARER_PREFIX = "Bearer ";
    var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    var securityContext = SecurityContextHolder.getContext();

    try {
      if (ObjectUtils.isEmpty(authorization)) {
        log.error("Authorization is Empty");
      }
      if (!ObjectUtils.isEmpty(authorization)
          && authorization.startsWith(BEARER_PREFIX)
          && securityContext.getAuthentication() == null) {

        var accessToken = authorization.substring(BEARER_PREFIX.length());
        var username = jwtService.getUsername(accessToken);
        var userDetails = memberService.loadUserByUsername(username);

        var authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
      }

      filterChain.doFilter(request, response);
    } catch (JwtException exception) {
      throw new JwtException(exception.getMessage());
    }
  }
}
