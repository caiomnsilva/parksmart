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

    public PaymentFactoryImpl(PaymentRepository paymentRepository, HourlyPaymentRepository hourlyPaymentRepository, RecurringPaymentRepository recurringPaymentRepository) {
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentRepository = hourlyPaymentRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
    }

    @Override
    public UUID createAndAssociatePayment(Vehicle vehicle, String type) {
        if (hasPendingHourlyPayments(vehicle.getVehiclePlate()) ||
                (vehicle.getAssociatedCustomer() != null && hasPendingRecurringPayments(vehicle.getAssociatedCustomer().getCpf()))) {
            throw new ApiException("O veículo possui pagamentos pendentes e não pode iniciar um novo pagamento ou entrar no estacionamento.", HttpStatus.BAD_REQUEST);
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

        throw new ApiException("Unknown payment type: " + payment.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    // Define o valor a ser pago de acordo com o tipo de pagamento por hora
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
        // Verifica se existem pagamentos pendentes, vencidos ou parciais para a placa do veículo
        List<HourlyPayment> payments = hourlyPaymentRepository.findByPayerVehicleVehiclePlateAndStatusIn(vehiclePlate
                , List.of(PaymentStatus.PENDING, PaymentStatus.OVERDUE, PaymentStatus.PARTIAL));
        return !payments.isEmpty();
    }

    private boolean hasPendingRecurringPayments(String cpf) {
        // Verifica se existem pagamentos pendentes, vencidos ou parciais para o CPF do cliente
        List<RecurringPayment> payments = recurringPaymentRepository.findByPayerCustomerCpfAndStatusIn(cpf
                , List.of(PaymentStatus.PENDING, PaymentStatus.PARTIAL));
        return !payments.isEmpty();
    }
}
