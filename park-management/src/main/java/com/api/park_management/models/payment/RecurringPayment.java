package com.api.park_management.models.payment;

import com.api.park_management.models.Customer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@DiscriminatorValue("RECURRING")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecurringPayment extends Payment {
    
    @PastOrPresent
    private LocalDateTime periodStart = LocalDateTime.now();

    @Future
    private LocalDateTime periodEnd;

    private boolean active = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    @JsonBackReference
    private Customer payerCustomer;

}