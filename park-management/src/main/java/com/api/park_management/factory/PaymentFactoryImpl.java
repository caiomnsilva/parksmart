package com.api.park_management.factory;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.models.Customer;
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
        // Se o veículo não tiver um cliente associado ou o cliente não for mensal, então o pagamento é por hora
       if(vehicle.getAssociatedCustomer() == null || vehicle.getAssociatedCustomer().getType() != CustomerType.MONTHLY){
           return new HourlyPayment();
       }
        return new RecurringPayment(); // Se o cliente for mensal, então o pagamento é recorrente
    }

    @Override
    public void createAndAssociatePayment(Vehicle vehicle) {
        Payment payment = createPayment(vehicle);
        // Se o pagamento for por hora, então associa o veículo ao pagamento
        if (payment instanceof HourlyPayment hourlyPayment) {
            hourlyPayment.setPayerVehicle(vehicle);
            paymentRepository.saveAndFlush(hourlyPayment);
        }else if (payment instanceof RecurringPayment recurringPayment) { // Se o pagamento for recorrente, então associa o cliente ao pagamento
            recurringPayment.setPayerCustomer(vehicle.getAssociatedCustomer());
            paymentRepository.saveAndFlush(recurringPayment);
        }

    }

}
