package com.api.park_management.factory;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.models.Customer;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.PaymentRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFactoryImpl implements PaymentFactory{
    private final PaymentRepository paymentRepository;

    public PaymentFactoryImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createPayment(Vehicle vehicle){
        // Se o veículo não tiver um cliente associado ou o cliente não for mensal, então o pagamento é por hora
       if(vehicle.getAssociatedCustomer() == null || vehicle.getAssociatedCustomer().getType() != CustomerType.MONTHLY){
           return new HourlyPayment();
       }
        return new RecurringPayment(); // Se o cliente for mensal, então o pagamento é recorrente
    }

    @Override
    public UUID createAndAssociatePayment(Vehicle vehicle) {
        Payment payment = createPayment(vehicle);

        // Se o pagamento for por hora, então associa o veículo ao pagamento
        if (payment instanceof HourlyPayment hourlyPayment) {
            hourlyPayment.setPayerVehicle(vehicle);
            paymentRepository.saveAndFlush(hourlyPayment);
            return hourlyPayment.getIdPayment();
        }

        // Se o pagamento for recorrente, então associa o cliente ao pagamento e define o valor a ser pago
        if (payment instanceof RecurringPayment recurringPayment) {
            recurringPayment.setPayerCustomer(vehicle.getAssociatedCustomer());
            recurringPayment.setAmountToPay(BigDecimal.valueOf(100.00));
            paymentRepository.saveAndFlush(recurringPayment);
            return recurringPayment.getIdPayment();
        }

        throw new IllegalArgumentException("Unknown payment type: " + payment.getClass().getName());
    }

}
