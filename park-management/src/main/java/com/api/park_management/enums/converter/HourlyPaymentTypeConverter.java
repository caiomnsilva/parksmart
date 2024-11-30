package com.api.park_management.enums.converter;

import com.api.park_management.enums.HourlyPaymentType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HourlyPaymentTypeConverter extends GenericEnumConverter<HourlyPaymentType> {
    public HourlyPaymentTypeConverter() {
        super(HourlyPaymentType.class);
    }
}
