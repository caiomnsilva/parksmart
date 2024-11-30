package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;
import com.api.park_management.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public enum PaymentStatus implements ConvertibleEnum{
    PAID("Pago"),
    PENDING("Pendente"),
    PARTIAL("Pagamento Parcial"),
    OVERDUE("Vencido");

    private final String value;

    private PaymentStatus(String value) {
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

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ApiException("Status de pagamento inválido: " + value, HttpStatus.BAD_REQUEST);
    }
}
