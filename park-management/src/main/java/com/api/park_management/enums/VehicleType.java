package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;

public enum VehicleType implements ConvertibleEnum {
    MOTORCYCLE("Moto"),
    CAR("Carro"),
    PCD("PCD");
    
    private final String value;

    private VehicleType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static VehicleType fromValue(String value) {
        for (VehicleType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de veículo inválido: " + value);
    }
}

