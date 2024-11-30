package com.api.park_management.enums;

import com.api.park_management.enums.converter.ConvertibleEnum;
import com.api.park_management.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public enum HourlyPaymentType implements ConvertibleEnum {
    HOURLY("Hora"),
    DAYLIGHT("Diurno"),
    NIGHT("Noturno");

    private final String value;

    private HourlyPaymentType(String value) {
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

    public static HourlyPaymentType fromValue(String value) {
        for (HourlyPaymentType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ApiException("Tipo de pagamento por hora inválido: " + value, HttpStatus.BAD_REQUEST);
    }
}
