package com.vkm_backend.teams.domain;

public record CompositionHash(String value) {
    public CompositionHash {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Composition Hash não pode ser nulo ou vazio");
        }
    }
}
