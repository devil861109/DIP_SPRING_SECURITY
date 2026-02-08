package edu.unam.springsecurity.security.jwt;

import edu.unam.springsecurity.security.model.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JWTTokenProvider {
    private static final String TOKEN_TYPE_CLAIM = "typ";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private String secret;
    private int jwtExpirationInMs;
    private int refreshExpirationInMs;
    private SecretKey key;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    @Value("${jwt.refreshExpirationDateInMs}")
    public void setRefreshExpirationInMs(int refreshExpirationInMs) {
        this.refreshExpirationInMs = refreshExpirationInMs;
    }

    public String generateJwtToken(UserDetailsImpl user) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs * 1000L);

        return Jwts.builder()
                .subject("UNAM")
                .issuer(user.getUsername())
                .audience().add("JAVA").and()
                .claim("principal", user)
                .claim("auth", user.getAuthorities())
                .claim("issid", user.getId())
                .claim("issname", user.getName())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetailsImpl user) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs * 1000L);

        return Jwts.builder()
                .subject("UNAM")
                .issuer(user.getUsername())
                .audience().add("JAVA").and()
                .claim("issid", user.getId())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getFullName(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        var body = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) body.get("issname");
    }

    public String getSubject(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getIssuer(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getIssuer();
    }

    public String getAudience(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        var audience = claims.getAudience();
        return audience != null && !audience.isEmpty() ? audience.iterator().next() : null;
    }

    public Date getTokenExpiryFromJWT(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    public Date getTokenIatFromJWT(String token) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getIssuedAt();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            key = Keys.hmacShaKeyFor(secret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
                log.error("Invalid JWT token type -> Message: {}", tokenType);
                return false;
            }
            return true;
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT token -> Message: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("Expired JWT token -> Message: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported JWT token -> Message: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims string is empty -> Message: {}", exception.getMessage());
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            key = Keys.hmacShaKeyFor(secret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
                log.error("Invalid refresh token type -> Message: {}", tokenType);
                return false;
            }
            return true;
        } catch (MalformedJwtException exception) {
            log.error("Invalid refresh token -> Message: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("Expired refresh token -> Message: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported refresh token -> Message: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Refresh token claims string is empty -> Message: {}", exception.getMessage());
        }
        return false;
    }

    public long getExpiryDuration() {
        return jwtExpirationInMs * 1000L;
    }

    public long getRefreshExpiryDuration() {
        return refreshExpirationInMs * 1000L;
    }
}