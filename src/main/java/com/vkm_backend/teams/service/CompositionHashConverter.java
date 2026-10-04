package com.vkm_backend.teams.service;

import com.vkm_backend.teams.domain.CompositionHash;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CompositionHashConverter implements AttributeConverter<CompositionHash, String> {

    @Override
    public String convertToDatabaseColumn(CompositionHash attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public CompositionHash convertToEntityAttribute(String dbData) {
        return dbData != null ? new CompositionHash(dbData) : null;
    }
}
