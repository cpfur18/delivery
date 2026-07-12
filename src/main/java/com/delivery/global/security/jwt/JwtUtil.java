package com.delivery.global.security.jwt;

import static com.delivery.global.config.JwtProperties.ACCESS_TOKEN_VALIDITY;
import static com.delivery.global.config.JwtProperties.REFRESH_TOKEN_VALIDITY;

import com.delivery.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil implements Serializable {
    @Serial private static final long serialVersionUID = -2634790745690120103L;
    private final JwtProperties jwtProperties;

    public String getUserUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromAccessToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token, Key signingKey) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getAllClaimsFromAccessToken(String token) {
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(jwtProperties.getAccessSecret()),
                        SignatureAlgorithm.HS256.getJcaName());
        return getAllClaimsFromToken(token, signingKey);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String generateToken(
            UserDetails userDetails, String userUuid, Key signingKey, long validity) {
        Map<String, Object> claims = new HashMap<>();
        Date date = new Date();

        claims.put("userUuid", userUuid);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + validity))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(UserDetails userDetails, String userUuid) {
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(jwtProperties.getAccessSecret()),
                        SignatureAlgorithm.HS256.getJcaName());
        return generateToken(userDetails, userUuid, signingKey, ACCESS_TOKEN_VALIDITY);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims getAllClaimsFromRefreshToken(String token) {
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(jwtProperties.getRefreshSecret()),
                        SignatureAlgorithm.HS256.getJcaName());
        return getAllClaimsFromToken(token, signingKey);
    }

    public String generateRefreshToken(UserDetails userDetails, String userUuid) {
        Key signingKey =
                new SecretKeySpec(
                        Base64.getDecoder().decode(jwtProperties.getRefreshSecret()),
                        SignatureAlgorithm.HS256.getJcaName());
        return generateToken(userDetails, userUuid, signingKey, REFRESH_TOKEN_VALIDITY);
    }

    public Boolean validateRefreshToken(String token, UserDetails userDetails) {
        String username = getUserUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
