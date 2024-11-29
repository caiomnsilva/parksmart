package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;

public enum SpotType implements ConvertibleEnum {
    MOTORCYCLE("Moto"),
    CAR("Carro"),
    PCD("PCD"),
    MONTHLY("Mensalista");

    private final String value;

    private SpotType(String value) {
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

    public static SpotType fromValue(String value) {
        for (SpotType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de vaga inválido: " + value);
    }
}
