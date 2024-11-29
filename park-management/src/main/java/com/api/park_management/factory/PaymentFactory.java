package com.api.park_management.factory;

import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.Payment;

public interface PaymentFactory {
    Payment createPayment(Vehicle vehicle);
    void createAndAssociatePayment(Vehicle vehicle);
}
