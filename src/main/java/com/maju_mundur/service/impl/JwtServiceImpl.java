package com.maju_mundur.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.maju_mundur.dto.response.JwtClaims;
import com.maju_mundur.entity.UserAccount;
import com.maju_mundur.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final String JWT_SECRET;
    private final String ISSUER;
    private final long JWT_EXPIRATION;

    public JwtServiceImpl(
            @Value("${maju_mundur.jwt.secret_key}") String JWT_SECRET,
            @Value("${maju_mundur.jwt.issuer}") String ISSUER,
            @Value("${maju_mundur.jwt.expirationInSecond}") long JWT_EXPIRATION
    ) {
        this.JWT_SECRET = JWT_SECRET;
        this.ISSUER = ISSUER;
        this.JWT_EXPIRATION = JWT_EXPIRATION;
    }

    @Override
    public String generateToken(UserAccount userAccount) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            return JWT.create()
                    .withSubject(userAccount.getId())
                    .withClaim("roles", userAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION))
                    .withIssuer(ISSUER)
                    .sign(algorithm);

        }catch (JWTCreationException exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error while creating jwt token");
        }
    }

    @Override
    public boolean verifyJwtToken(String bearerToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            String token = parseJwt(bearerToken);
            assert token != null;
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            log.error("Invalid JWT Signature : {}", exception.getMessage());
            return false;
        }
    }

    @Override
    public JwtClaims getClaimsByToken(String bearerToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            String token = parseJwt(bearerToken);
            assert token != null;
            DecodedJWT decodedJWT = verifier.verify(token);
            return JwtClaims.builder()
                    .userAccountId(decodedJWT.getSubject())
                    .roles(decodedJWT.getClaim("roles").asList(String.class))
                    .build();
        } catch (JWTVerificationException exception){
            // Invalid JWT Signature/Claims
            log.error("Invalid JWT Claims : {}", exception.getMessage());
            return null;
        }
    }

    private String parseJwt(String token) {
        log.error("Test token : {}", token);
        if(token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
