package com.api.park_management.enums.converter;

import jakarta.persistence.Converter;
import com.api.park_management.enums.SpotType;

@Converter(autoApply = true)
public class SpotTypeConverter extends GenericEnumConverter<SpotType> {
    public SpotTypeConverter() {
        super(SpotType.class);
    }
}
