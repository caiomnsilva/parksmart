package com.api.park_management.factory;

import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.Payment;

import java.util.UUID;

public interface PaymentFactory {
    UUID createAndAssociatePayment(Vehicle vehicle, String type);
    Payment createPayment(Vehicle vehicle);
}
