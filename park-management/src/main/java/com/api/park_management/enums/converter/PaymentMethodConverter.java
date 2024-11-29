package com.api.park_management.enums.converter;

import jakarta.persistence.Converter;
import com.api.park_management.enums.PaymentMethod;

@Converter(autoApply = true)
public class PaymentMethodConverter extends GenericEnumConverter<PaymentMethod> {
    public PaymentMethodConverter() {
        super(PaymentMethod.class);
    }
}
