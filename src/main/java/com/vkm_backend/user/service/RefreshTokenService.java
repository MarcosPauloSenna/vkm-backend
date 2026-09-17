package com.vkm_backend.user.service;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.EncryptionException;
import com.vkm_backend.user.infra.persistence.RefreshTokenEntity;
import com.vkm_backend.user.infra.persistence.RefreshTokenRepository;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;


    @Value("${api.security.refresh-token.expiration-days}")
    private long expirationDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;

    }

    public String create(String username){

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("Usuário não encontrado");
        }
        String tokenFamily = UUID.randomUUID().toString();
        String rawToken = generateRawToken();

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setTokenHash(hash(rawToken));
        entity.setTokenFamily(tokenFamily);
        entity.setExpiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS));

        refreshTokenRepository.save(entity);

        return rawToken;
    }

    private String generateRawToken(){
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hash(String token) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e.getMessage());
        }


    }

}
