package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;

public enum PaymentMethod implements ConvertibleEnum {
    CASH("Dinheiro"),
    CREDIT_CARD("Cartão de Crédito"),
    DEBIT_CARD("Cartão de Débito"),
    PIX("PIX");

    private final String value;

    private PaymentMethod(String value) {
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

    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.getValue().equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pagamento inválido: " + value);
    }
}
