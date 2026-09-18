package com.vkm_backend.user.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    Optional<RefreshTokenEntity> findByTokenHashAndRevokedAtNull( String tokenHash);

    Optional<RefreshTokenEntity> findByTokenFamily(String tokenFamily );

    @Modifying
    @Query("""
    update RefreshTokenEntity token
       set token.revokedAt = :revokedAt
     where token.tokenFamily = :tokenFamily
       and token.revokedAt is null
""")
    void revokeFamily(String tokenFamily, Instant revokedAt);

    @Modifying
    @Query("""
    update RefreshTokenEntity token
       set token.revokedAt = :revokedAt
     where token.user.id = :userId
       and token.revokedAt is null
""")
    void revokeAllByUserId(Long userId, Instant revokedAt);
}
