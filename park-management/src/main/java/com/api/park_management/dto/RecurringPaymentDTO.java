package com.api.park_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.api.park_management.enums.PaymentMethod;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record RecurringPaymentDTO(
        @JsonProperty("_id") UUID idPayment,
        @Positive BigDecimal paidAmount,
        @Positive BigDecimal amountToPay,
        @NotBlank @ValueOfEnum(enumClass = PaymentMethod.class) String method,
        @NotBlank @ValueOfEnum(enumClass = PaymentStatus.class) String status,
        @PastOrPresent LocalDateTime paymentDate,
        @PastOrPresent LocalDateTime periodStart,
        @Future LocalDateTime periodEnd,
        @NotBlank boolean active,
        String customerCpf
) {}
