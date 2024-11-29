package com.api.park_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.api.park_management.enums.PaymentMethod;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record HourlyPaymentDTO(
        @JsonProperty("_id") UUID idPayment,
        @Positive BigDecimal paidAmount,
        @Positive BigDecimal amountToPay,
        @ValueOfEnum(enumClass = PaymentMethod.class) String method,
        @NotBlank @ValueOfEnum(enumClass = PaymentStatus.class) String status,
        @PastOrPresent LocalDateTime paymentDate,
        @Positive Double hoursParked,
        @PastOrPresent LocalDateTime entryTime,
        @FutureOrPresent LocalDateTime exitTime,
        String vehiclePlate
) {}
