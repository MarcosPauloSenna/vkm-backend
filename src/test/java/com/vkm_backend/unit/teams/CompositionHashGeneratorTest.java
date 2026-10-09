package com.vkm_backend.unit.teams;

import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.service.CompositionHashGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompositionHashGeneratorTest {

    private final CompositionHashGenerator generator = new CompositionHashGenerator();

    @Test
    void shouldGenerateSameHashRegardlessOfMemberOrder() {
        CompositionHash first = generator.generate(List.of(35L, 10L, 22L));
        CompositionHash reordered = generator.generate(List.of(22L, 35L, 10L));

        assertThat(reordered).isEqualTo(first);
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentCompositions() {
        assertThat(generator.generate(List.of(10L, 22L)))
                .isNotEqualTo(generator.generate(List.of(10L, 23L)));
    }

    @Test
    void shouldGenerateSha256HexadecimalHash() {
        assertThat(generator.generate(List.of(10L)).value())
                .matches("[0-9a-f]{64}");
    }
}
