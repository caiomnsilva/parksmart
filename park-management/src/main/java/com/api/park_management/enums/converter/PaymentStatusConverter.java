package com.api.park_management.enums.converter;

import com.api.park_management.enums.PaymentStatus;

import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class PaymentStatusConverter extends GenericEnumConverter<PaymentStatus>{
    public PaymentStatusConverter(){
        super(PaymentStatus.class);
    }
}
