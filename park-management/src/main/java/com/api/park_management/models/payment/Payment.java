package com.api.park_management.models.payment;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.api.park_management.enums.PaymentMethod;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.enums.converter.PaymentMethodConverter;
import com.api.park_management.enums.converter.PaymentStatusConverter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;


@Entity
@Table(name = "TB_PAYMENTS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "payment_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class Payment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPayment;

    @PositiveOrZero
    private BigDecimal paidAmount;

    @PositiveOrZero
    private BigDecimal amountToPay = BigDecimal.valueOf(8.00);

    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethod method = PaymentMethod.UNDEFINED;

    @PastOrPresent
    private LocalDateTime paymentDate;

    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus status = PaymentStatus.PENDING;

}
