package com.api.park_management.factory;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.enums.HourlyPaymentType;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.exceptions.ApiException;
import com.api.park_management.models.Customer;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.HourlyPaymentRepository;
import com.api.park_management.repositories.PaymentRepository;
import com.api.park_management.repositories.RecurringPaymentRepository;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class PaymentFactoryImpl implements PaymentFactory{
    private final PaymentRepository paymentRepository;
    private final HourlyPaymentRepository hourlyPaymentRepository;
    private final RecurringPaymentRepository recurringPaymentRepository;
    private final VehicleRepository vehicleRepository;

    public PaymentFactoryImpl(PaymentRepository paymentRepository, HourlyPaymentRepository hourlyPaymentRepository, RecurringPaymentRepository recurringPaymentRepository, VehicleRepository vehicleRepository) {
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentRepository = hourlyPaymentRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public UUID createAndAssociatePayment(Vehicle vehicle, String type) {
        if (hasPendingHourlyPayments(vehicle.getVehiclePlate())) {

            System.out.println("Vehicle Plate: " + vehicle.getVehiclePlate());
            System.out.println("hasPendingHourlyPayments: " + hasPendingHourlyPayments(vehicle.getVehiclePlate()));

            throw new ApiException("O veículo possui pagamentos por hora pendentes e não pode iniciar um novo pagamento.", HttpStatus.BAD_REQUEST);
        }

        if (vehicle.getAssociatedCustomer() != null && hasPendingRecurringPayments(vehicle.getAssociatedCustomer().getCpf())) {

            System.out.println("CPF: " + vehicle.getAssociatedCustomer().getCpf());

            System.out.println("hasPendingRecurringPayments: " + hasPendingRecurringPayments(vehicle.getAssociatedCustomer().getCpf()));

            throw new ApiException("O cliente associado possui pagamentos recorrentes pendentes.", HttpStatus.BAD_REQUEST);
        }

        Payment payment = createPayment(vehicle);

        // Se o pagamento for por hora, então associa o veículo ao pagamento
        if (payment instanceof HourlyPayment hourlyPayment) {
            hourlyPayment.setPayerVehicle(vehicle);
            payment.setAmountToPay(setHourlyPaymentAmoutToPay(type));
            paymentRepository.saveAndFlush(hourlyPayment);
            setHourlyPaymentType(hourlyPayment.getIdPayment(), type);
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

        throw new ApiException("Tipo de pagamento desconhecido: " + payment.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Payment createPayment(Vehicle vehicle) {
        Customer customer = vehicle.getAssociatedCustomer();

        // Se o veículo não tiver um cliente associado ou o cliente não for mensal, então o pagamento é por hora
        if (customer == null || customer.getType() != CustomerType.MONTHLY) {
            return new HourlyPayment();
        }

        // Verifique se o cliente já possui um pagamento recorrente ativo
        if (hasActiveRecurringPayment(customer)) {
            throw new ApiException("Já existe um pagamento recorrente ativo para este cliente.", HttpStatus.CONFLICT);
        }

        // Se o cliente for mensal e não tiver um pagamento recorrente ativo, então crie um novo pagamento recorrente
        return new RecurringPayment();
    }

    private boolean hasActiveRecurringPayment(Customer customer) {
        List<RecurringPayment> payments = recurringPaymentRepository.findByPayerCustomerCpfAndStatusIn(
                customer.getCpf(), List.of(PaymentStatus.PENDING, PaymentStatus.PARTIAL, PaymentStatus.PAID)
        );
        return payments.stream().anyMatch(payment -> LocalDateTime.now().isBefore(payment.getPeriodEnd()));
    }

    // Define o tipo de pagamento por hora
    private void setHourlyPaymentType(UUID id, String type){
        HourlyPayment payment = hourlyPaymentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Pagamento por hora não encontrado", HttpStatus.NOT_FOUND));

        try {
            payment.setType(HourlyPaymentType.valueOf(type));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Tipo de pagamento por hora inválido: " + type, HttpStatus.BAD_REQUEST);
        }

        hourlyPaymentRepository.saveAndFlush(payment);
    }


    private BigDecimal setHourlyPaymentAmoutToPay(String type){
        if(HourlyPaymentType.valueOf(type) == HourlyPaymentType.NIGHT){
            return BigDecimal.valueOf(35.00);
        }

        if(HourlyPaymentType.valueOf(type) == HourlyPaymentType.DAYLIGHT){
            return BigDecimal.valueOf(20.00);
        }

        if(HourlyPaymentType.valueOf(type) == HourlyPaymentType.HOURLY){
            return BigDecimal.valueOf(5.00);
        }

        throw new ApiException("Tipo de pagamento por hora inválido: " + type, HttpStatus.BAD_REQUEST);
    }

    private boolean hasPendingHourlyPayments(String vehiclePlate) {
        Vehicle v = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veículo não encontrado", HttpStatus.NOT_FOUND));


        if (v.getAssociatedCustomer() == null || v.getAssociatedCustomer().getType() != CustomerType.MONTHLY) {

            List<HourlyPayment> payments = hourlyPaymentRepository.findByPayerVehicleVehiclePlateAndStatusIn(vehiclePlate,
                    List.of(PaymentStatus.PENDING, PaymentStatus.OVERDUE, PaymentStatus.PARTIAL));

            HourlyPayment overduePayment = hourlyPaymentRepository.findByPayerVehicleVehiclePlateAndExitTimeIsNullAndTimeToLeaveIsNotNullAndTimeToLeaveBefore(vehiclePlate, LocalDateTime.now());
            if (overduePayment != null) {
                payments.add(overduePayment);
            }

            HourlyPayment openPayment = hourlyPaymentRepository.findByPayerVehicleVehiclePlateAndExitTimeIsNull(vehiclePlate);
            if (openPayment != null) {
                payments.add(openPayment);
            }

            return !payments.isEmpty();
        }

        return false;
    }

    private boolean hasPendingRecurringPayments(String cpf) {
        List<RecurringPayment> payments = recurringPaymentRepository.findByPayerCustomerCpfAndStatusIn(cpf
                , List.of(PaymentStatus.PENDING, PaymentStatus.PARTIAL));
        return !payments.isEmpty();
    }
}
