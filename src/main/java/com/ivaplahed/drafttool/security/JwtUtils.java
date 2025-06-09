package com.ivaplahed.drafttool.security;

import com.ivaplahed.drafttool.security.model.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.token}")
    private String jwtSecret;

    @Value("${jwt.access.token.expiration.millis}")
    private int accessTokenExpirationMillis;

    @PostConstruct
    public void init() {
        logger.info("Configured JWT Secret (first 10 chars): {}", jwtSecret != null ? jwtSecret.substring(0, Math.min(jwtSecret.length(), 10)) + "..." : "N/A");
        logger.info("Configured JWT Access Token Expiration (millis): {}", accessTokenExpirationMillis);
    }

    public String generateAccessToken(Authentication auth) {

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();

        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String fullName = user.getFirstName() + " " + user.getLastName();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("name", fullName)
                .claim("roles", roles)
                .claim("tokenType", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMillis))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("JWT Validation Error: Invalid JWT token: {} - Token: {}", e.getMessage(), token);
        } catch (ExpiredJwtException e) {
            logger.error("JWT Validation Error: JWT token is expired: {} - Token: {}", e.getMessage(), token);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT Validation Error: JWT token is unsupported: {} - Token: {}", e.getMessage(), token);
        } catch (IllegalArgumentException e) {
            logger.error("JWT Validation Error: JWT claims string is empty or invalid: {} - Token: {}", e.getMessage(), token);
        } catch (SignatureException e) {
            logger.error("JWT Validation Error: Invalid JWT signature: {} - Token: {}", e.getMessage(), token); // This is a strong candidate!
        } catch (Exception e) {
            logger.error("JWT Validation Error: An unexpected error occurred: {} - Token: {}", e.getMessage(), token, e);
        }
        return false;
    }

    private SecretKey key() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        } catch (IllegalArgumentException e) {
            logger.error("JWT secret is not a valid Base64 string or is empty.", e);
            throw new RuntimeException("Invalid JWT secret configuration", e);
        }
    }
}
