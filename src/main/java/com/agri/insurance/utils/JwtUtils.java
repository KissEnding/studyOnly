package com.agri.insurance.utils;

import com.agri.insurance.model.Admin;
import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.model.Farmer;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(Object user) {
        Map<String, Object> claims = new HashMap<>();
        String username;
        String role;

        if (user instanceof Admin) {
            username = ((Admin) user).getUsername();
            role = "ADMIN";
            log.info("Generating token for admin: {}", username);
        } else if (user instanceof Farmer) {
            username = ((Farmer) user).getUsername();
            role = "FARMER";
        } else if (user instanceof InsuranceCompany) {
            username = ((InsuranceCompany) user).getUsername();
            role = "COMPANY";
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }

        claims.put("username", username);
        claims.put("role", role);
        log.info("Token claims: {}", claims);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }

    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
} 