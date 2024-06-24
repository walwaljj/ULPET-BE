package com.overcomingroom.ulpet.auth.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

  private static final SecretKey key = SIG.HS256.key().build();
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String REFRESH_TOKEN = "refreshToken";

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(userDetails.getUsername(), ACCESS_TOKEN);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(userDetails.getUsername(), REFRESH_TOKEN);
  }

  public String getUsername(String token) {
    return getSubject(token);
  }

  private String generateToken(String subject, String tokenType) {
    var now = new Date();
    var exp = tokenType.equals(ACCESS_TOKEN) ? new Date(now.getTime() + (1000 * 60 * 60 * 3)) : new Date(now.getTime() + (1000 * 60 * 60 * 24 * 7));
    return Jwts.builder().subject(subject).signWith(key).issuedAt(now).expiration(exp).compact();
  }

  private String getSubject(String token) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    } catch (JwtException e) {
      log.error("JwtException", e);
      throw e;
    }
  }

}
