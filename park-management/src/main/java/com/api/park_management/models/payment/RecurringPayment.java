package com.api.park_management.models.payment;

import com.api.park_management.models.Customer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("RECURRING")
@Getter
@Setter
public class RecurringPayment extends Payment {
    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer associatedCustomer;

}
