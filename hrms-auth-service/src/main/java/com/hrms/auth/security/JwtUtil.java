package com.hrms.auth.security;

import com.hrms.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private String generateToken(
            Map<String, Object> claims,
            String subject,
            long expiration
    ) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();


        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);


        if (user.getEmployeeId() != null) {
            claims.put("employeeId", user.getEmployeeId());
        }

        return generateToken(
                claims,
                user.getUsername(),
                expirationMs
        );
    }


    public String generateRefreshToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return generateToken(
                claims,
                user.getUsername(),
                refreshExpirationMs
        );
    }


    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
