package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;

public enum CustomerType implements ConvertibleEnum {
    REGULAR("Regular"),
    VIP("VIP"),
    MONTHLY("Mensalista");

    private final String value;

    private CustomerType(String value) {
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

    public static CustomerType fromValue(String value) {
        for (CustomerType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de cliente inválido: " + value);
    }
}
