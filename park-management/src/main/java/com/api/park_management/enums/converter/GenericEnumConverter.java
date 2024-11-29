package com.api.park_management.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


import java.util.stream.Stream;

@Converter(autoApply = true)
public class GenericEnumConverter<T extends Enum<T> & ConvertibleEnum> implements AttributeConverter<T, String> {

    private final Class<T> enumClass;

    public GenericEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Stream.of(enumClass.getEnumConstants())
                .filter(e -> e.getValue().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

