package com.vkm_backend.infra.global.mapper;

/**
 * Contrato comum dos mappers do projeto: entidade (persistência) -> domínio -> response (web).
 *
 * @param <E> entidade JPA
 * @param <D> objeto de domínio
 * @param <R> response devolvido pela API
 */
public interface EntityMapper<E, D, R> {

    E toEntity(D domain);

    D toDomain(E entity);

    R toResponse(D domain);

    default R toResponseFromEntity(E entity) {
        return toResponse(toDomain(entity));
    }
}
