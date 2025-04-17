package com.saksham.booking_application.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saksham.booking_application.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService extends JwtValidator {

    private final Map<String, String> jsonHeaderPayload = Map.of("alg", "RS512", "typ", "JWT");
    public final static String ACCESS_TOKEN = "accessToken";
    public final static String REFRESH_TOKEN = "refreshToken";
    protected int jwtAccessTokenExpireDuration;
    protected int jwtRefreshTokenExpireDuration;

    protected final static String REFESH_TOKENS_KEY = "users.refresh.token";

    public JwtService(@Value("${jwt.access_token.expire}") int jwtAccessTokenExpireDuration,
            @Value("${jwt.refresh_token.expire}") int jwtRefreshTokenExpireDuration,
            @Value("${jwt.public.key}") String publicKeyPEM,
            @Value("${jwt.private.key}") String privateKeyPEM) {
        super(publicKeyPEM, privateKeyPEM);
        this.jwtAccessTokenExpireDuration = jwtAccessTokenExpireDuration;
        this.jwtRefreshTokenExpireDuration = jwtRefreshTokenExpireDuration;
    }

    public Map<String, String> createTokens(User user) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN, this.createAccessToken(user));
        tokens.put(REFRESH_TOKEN, this.createRefreshToken(user));
        return tokens;
    }

    private String createAccessToken(User user) {
        try {
            Instant currentInstant = Instant.now();
            Instant expiration = currentInstant.plus(Duration.ofMinutes(jwtAccessTokenExpireDuration));
            String jsonHeader = objectMapper.writeValueAsString(jsonHeaderPayload);
            String tokenPayload = getTokenPayload(user, currentInstant, expiration, false);

            String header = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(jsonHeader.getBytes(StandardCharsets.UTF_8));

            String payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(tokenPayload.getBytes(StandardCharsets.UTF_8));

            String signedContent = header + "." + payload;
            byte[] signature = signWithPrivateKey(signedContent);

            return signedContent + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            log.error("Error creating JWT token: ", e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private String createRefreshToken(User user) {
        try {
            Instant currentInstant = Instant.now();
            Instant expiration = currentInstant.plus(Duration.ofHours(jwtRefreshTokenExpireDuration));
            String jsonHeader = objectMapper.writeValueAsString(jsonHeaderPayload);
            String tokenPayload = getTokenPayload(user, currentInstant, expiration, true);

            String header = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(jsonHeader.getBytes(StandardCharsets.UTF_8));

            String payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(tokenPayload.getBytes(StandardCharsets.UTF_8));

            String signedContent = header + "." + payload;
            byte[] signature = signWithPrivateKey(signedContent);

            return signedContent + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            log.error("Error creating JWT refresh token: ", e);
            throw new RuntimeException("Error generating JWT refresh token: ", e);
        }
    }

    private byte[] signWithPrivateKey(String signedContent) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(SHA_512_WITH_RSA_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(signedContent.getBytes());
        return signature.sign();
    }

    private String getTokenPayload(User user, Instant currentTime, Instant expiration, boolean isRefresh)
            throws JsonProcessingException {
        if (isRefresh) {
            return objectMapper.writeValueAsString(
                    Map.of("username", user.getUsername(), "type", "refresh", "iat", currentTime.getEpochSecond(),
                            "exp",
                            expiration.getEpochSecond()));
        }
        return objectMapper.writeValueAsString(Map.of(
                "username", user.getUsername(), "email", user.getEmail(), "isAdmin", user.getIsAdmin(), "type", "token",
                "iat",
                currentTime.getEpochSecond(), "exp",
                expiration.getEpochSecond()));
    }
}