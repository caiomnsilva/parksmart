package com.api.park_management.factory;

import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.Payment;

import java.util.UUID;

public interface PaymentFactory {
    Payment createPayment(Vehicle vehicle);
    UUID createAndAssociatePayment(Vehicle vehicle);
}
