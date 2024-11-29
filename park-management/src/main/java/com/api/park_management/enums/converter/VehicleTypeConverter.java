package com.api.park_management.enums.converter;

import jakarta.persistence.Converter;
import com.api.park_management.enums.VehicleType;

@Converter(autoApply = true)
public class VehicleTypeConverter extends GenericEnumConverter<VehicleType> {
    public VehicleTypeConverter() {
        super(VehicleType.class);
    }
}
