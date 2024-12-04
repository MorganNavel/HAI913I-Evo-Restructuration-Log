package com.example.tp3logging.services;

import com.example.tp3logging.models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${JWT_SECRET}")
    private String secret;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * Generate a JWT token for a given user.
     *
     * @param user the user for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getUserId() + "")
                .withClaim("mail", user.getEmail())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .withJWTId(UUID.randomUUID().toString())
                .sign(getAlgorithm());
    }

    /**
     * Validate the JWT token's integrity and expiration.
     *
     * @param token the JWT token to be validated
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(getAlgorithm()).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the email from the JWT token.
     *
     * @param token the JWT token
     * @return the email if the token is valid, otherwise null
     */
    public String getEmailFromToken(String token) {
        if (validateToken(token)) {
            return JWT.decode(token).getClaim("mail").asString();
        }
        return null;
    }

    /**
     * Get the user ID from the JWT token.
     *
     * @param token the JWT token
     * @return the user ID if the token is valid, otherwise -1
     */
    public int getUserIdFromToken(String token) {
        if (validateToken(token)) {
            return Integer.parseInt(JWT.decode(token).getSubject());
        }
        return -1;
    }
}