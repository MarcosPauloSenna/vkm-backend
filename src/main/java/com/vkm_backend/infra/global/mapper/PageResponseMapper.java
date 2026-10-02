package com.vkm_backend.infra.global.mapper;

import com.vkm_backend.infra.global.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Converte uma {@link Page} de entidades em {@link PageResponse}, centralizando a montagem
 * dos metadados de paginação (totalElements, totalPages, first, last...).
 */
@Component
public class PageResponseMapper {

    public <E, D, R> PageResponse<R> toPageResponse(Page<E> page, EntityMapper<E, D, R> mapper) {
        return toPageResponse(page, mapper::toResponseFromEntity);
    }

    public <E, R> PageResponse<R> toPageResponse(Page<E> page, Function<E, R> converter) {
        Page<R> responsePage = page.map(converter);

        return new PageResponse<>(responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isFirst(),
                responsePage.isLast(),
                responsePage.hasNext(),
                responsePage.hasPrevious());
    }
}
