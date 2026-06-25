package org.racetobid.racetobid.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.refresh}")
    private String refreshSecretKey;

    private final static Logger log = LoggerFactory.getLogger(JwtTokenService.class);


    public String generateAccessToken(UserDetails userDetails){
        String email = userDetails.getUsername();
        log.info("JwtTokenService - Generating Access Token for email: " + email);
        String token = createToken(new HashMap<>(), email, 1000 * 60 * 15, getSignKey(secretKey));
        log.info("JwtTokenService - Token generated successfully. Length: " + token.length());

        // Token'ı decode edip içeriğini kontrol et
        try {
            String extractedEmail = extractEmail(token);
            Date expiration = extractExpiration(token);
            log.info("JwtTokenService - Token verification: Extracted Email: " + extractedEmail + ", Expiration: " + expiration);
        } catch (Exception e) {
            log.error("JwtTokenService - Error verifying generated token: " + e.getMessage());
        }

        return token;
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), 1000L * 60 * 60 * 24 * 7, getSignKey(refreshSecretKey)); // 7 gün
    }
    private String createToken(Map<String,Object> claims, String email, long expiration, SecretKey key){
        Date now = new Date(System.currentTimeMillis());
        Date exp = new Date(System.currentTimeMillis() + expiration);

        log.info("JwtTokenService - Creating token with email: " + email + ", IssuedAt: " + now + ", Expiration: " + exp);

        String token = Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();

        log.info("JwtTokenService - Token created successfully");
        return token;
    }

    private Claims extractAllClaims(String token) {
        try {
            SecretKey key = getSignKey(secretKey);
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JwtTokenService - Error extracting claims from token: " + e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolve){
        final Claims claims=extractAllClaims(token);
        return claimsResolve.apply(claims);
    }
    public String extractEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public  Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String email=extractEmail(token);
        Boolean isExpired = isTokenExpired(token);
        log.info("JwtTokenService - Validating Token: Extracted Email: " + email + ", UserDetails Username: " + userDetails.getUsername() + ", Is Expired: " + isExpired);
        return (email.equals(userDetails.getUsername()) && !isExpired);
    }


    private SecretKey getSignKey(String secretKey){
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            log.debug("JwtTokenService - Sign key created successfully. Key length: " + keyBytes.length + " bytes");
            return key;
        } catch (Exception e) {
            log.error("JwtTokenService - Error creating sign key: " + e.getMessage());
            throw new RuntimeException("Failed to create sign key", e);
        }
    }

}
