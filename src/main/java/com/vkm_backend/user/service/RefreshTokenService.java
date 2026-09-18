package com.vkm_backend.user.service;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.EncryptionException;
import com.vkm_backend.user.infra.persistence.RefreshTokenEntity;
import com.vkm_backend.user.infra.persistence.RefreshTokenRepository;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import com.vkm_backend.user.infra.web.dto.RefreshTokenResult;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
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

    private final AccessTokenService accessTokenService;


    @Value("${api.security.refresh-token.expiration-days}")
    private long expirationDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               AccessTokenService accessTokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;

        this.accessTokenService = accessTokenService;
    }

    @Transactional
    public String create(String username, String tokenFamily){

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("Usuário não encontrado");
        }

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

    @Transactional
    public RefreshTokenResult refresh(String refreshToken){
        String tokenHash = hash(refreshToken);

        RefreshTokenEntity currentToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ValidationException("Refresh token inválido"));

        Instant now = Instant.now();

        if (currentToken.getRevokedAt() != null){
            refreshTokenRepository.revokeFamily(currentToken.getTokenFamily(), now);

            throw new ValidationException("Refresh token revogado! Para sua segurança todas as sessões foram encerrradas. Faça novo login!");
        }
        if (currentToken.getExpiresAt().isBefore(now)){
            throw new ValidationException("Refresh token expirado");
        }

        UserEntity user = currentToken.getUser();

        currentToken.setRevokedAt(now);
        currentToken.setLastUsedAt(now);
        refreshTokenRepository.save(currentToken);

        String newAccessToken = accessTokenService.generateAccessTokens(user);

        String newRefreshToken = create(user.getUsername(), currentToken.getTokenFamily());

        return new RefreshTokenResult(newAccessToken, newRefreshToken);

    }

    @Transactional
    public void logout(String rawToken) {
        String tokenHash = hash(rawToken);

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    if (token.getRevokedAt() == null) {
                        token.setRevokedAt(Instant.now());
                        refreshTokenRepository.save(token);
                    }
                });
    }

}
