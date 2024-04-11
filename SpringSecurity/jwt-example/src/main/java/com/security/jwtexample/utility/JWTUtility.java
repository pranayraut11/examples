package com.security.jwtexample.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Slf4j
public class JWTUtility {
    private static final Key SECRET = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String username) {
        var now = Instant.now();
        return Jwts.builder().setSubject(username)
                .signWith(SECRET)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(60, ChronoUnit.MINUTES)))
                .compact();
    }

    public static String getUsername(String token) {
        return getBody(token).getSubject();
    }
    public static boolean isValidToken(String token, String username) {
        boolean isValid = false;
        try {
            String subject = getUsername(token);
            log.info("Subject {} ", subject);
            if (StringUtils.hasText(subject) && subject.equals(username) && !isTokenExpired(token)) {
                isValid = true;
            }
        } catch (Exception e) {
            log.error("Error while parsing token", e);
        }
        return isValid;
    }

    private static Claims getBody(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token).getBody();
    }

    public static boolean isTokenExpired(String token) {
        Date expiration = getBody(token).getExpiration();
        log.info("Expiration {} ", expiration);
        log.info("Current Date {} ", new Date());
        return getBody(token).getExpiration().before(new Date());
    }
}
