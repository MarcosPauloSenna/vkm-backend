package com.vkm_backend.teams.service;

import com.vkm_backend.teams.domain.CompositionHash;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HexFormat;
import java.util.stream.Collectors;

@Service
public final class CompositionHashGenerator {

    public CompositionHashGenerator() {
    }

    public CompositionHash generate(Collection<Long> memberIds) {
        String composition = memberIds.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));

        return new CompositionHash(sha256(composition));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível", e);
        }
    }
}
