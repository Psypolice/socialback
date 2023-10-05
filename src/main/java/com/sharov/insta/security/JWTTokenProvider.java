package com.sharov.insta.security;

import com.sharov.insta.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.ZoneId.systemDefault;

@Slf4j
@Component
public class JWTTokenProvider {

    public String generateToken(Authentication authentication) {
        var user = (User) authentication.getPrincipal();
        var now = LocalDateTime.now(systemDefault());
        var expiryDate = now.plusSeconds(SecurityConstants.EXPIRATION_TIME);

        var userId = Long.toString(user.getId());

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getEmail());
        claimsMap.put("firstname", user.getName());
        claimsMap.put("lastname", user.getLastname());


        log.info("User " + user.getUsername() + " with user ID: " + userId +  " connected");

        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(Date.from(now.atZone(systemDefault()).toInstant()))
                .setExpiration(java.util.Date.from(expiryDate.atZone(systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException |
                MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        var claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");

        return Long.parseLong(id);
    }
}
