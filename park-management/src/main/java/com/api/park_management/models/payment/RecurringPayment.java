package com.api.park_management.models.payment;

import com.api.park_management.models.Customer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;

import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecurringPayment extends Payment {
    
    @PastOrPresent
    private LocalDateTime periodStart = LocalDateTime.now();

    @Future
    private LocalDateTime periodEnd;

    @Column(nullable = false)
    private boolean active = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer payerCustomer;

}