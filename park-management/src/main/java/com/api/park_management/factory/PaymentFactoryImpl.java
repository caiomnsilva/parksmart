package com.api.park_management.factory;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.PaymentRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactoryImpl implements PaymentFactory{
    private final PaymentRepository paymentRepository;

    public PaymentFactoryImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createPayment(Vehicle vehicle){
       if(vehicle.getAssociatedCustomer() == null || !vehicle.getAssociatedCustomer().getType().equals(CustomerType.MONTHLY)){
           return new HourlyPayment();
       }
        return new RecurringPayment();
    }

    @Override
    public void createAndAssociatePayment(Vehicle vehicle){
        Payment payment = createPayment(vehicle);
        paymentRepository.saveAndFlush(payment);

        if (payment instanceof HourlyPayment hourlyPayment) {
            hourlyPayment.setPayerVehicle(vehicle);
        }

        assert payment instanceof RecurringPayment;
        vehicle.getAssociatedCustomer().getPayments().add((RecurringPayment) payment);
    }

}
