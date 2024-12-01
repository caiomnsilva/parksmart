package com.api.park_management.services;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.dto.mapper.HourlyPaymentMapper;
import com.api.park_management.dto.mapper.RecurringPaymentMapper;
import com.api.park_management.enums.HourlyPaymentType;
import com.api.park_management.enums.PaymentMethod;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.exceptions.ApiException;
import com.api.park_management.factory.PaymentFactory;
import com.api.park_management.models.Customer;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final HourlyPaymentMapper hourlyPaymentMapper;
    private final RecurringPaymentMapper recurringPaymentMapper;
    private final PaymentFactory paymentFactory;
    private final VehicleRepository vehicleRepository;
    private final HourlyPaymentRepository hourlyPaymentRepository;
    private final RecurringPaymentRepository recurringPaymentRepository;
    private final CustomerRepository customerRepository;

    public PaymentService(PaymentRepository paymentRepository, HourlyPaymentMapper hourlyPaymentMapper, RecurringPaymentMapper recurringPaymentMapper, PaymentFactory paymentFactory, VehicleRepository vehicleRepository, HourlyPaymentRepository hourlyPaymentRepository, RecurringPaymentRepository recurringPaymentRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentMapper = hourlyPaymentMapper;
        this.recurringPaymentMapper = recurringPaymentMapper;
        this.paymentFactory = paymentFactory;
        this.vehicleRepository = vehicleRepository;
        this.hourlyPaymentRepository = hourlyPaymentRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.customerRepository = customerRepository;
    }

    public List<RecurringPaymentDTO> getAllRecurringPayments(){
        return paymentRepository.findAllRecurringPayments()
                .stream()
                .map(recurringPaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<HourlyPaymentDTO> getAllHourlyPayments(){
        return paymentRepository.findAllHourlyPayments()
                .stream()
                .map(hourlyPaymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RecurringPaymentDTO getRecurringPaymentById(UUID id){
        RecurringPayment payment = paymentRepository.findRecurringPaymentById(id);
        return recurringPaymentMapper.toDTO(payment);
    }

    public HourlyPaymentDTO getHourlyPaymentById(UUID id){
        HourlyPayment payment = paymentRepository.findHourlyPaymentById(id);
        return hourlyPaymentMapper.toDTO(payment);
    }

    @Transactional
    public Object createAndAssociatePayment(String vehiclePlate, String type) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        Payment payment = paymentRepository.findById(paymentFactory.createAndAssociatePayment(vehicle, type))
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        paymentRepository.save(payment);

        if (payment instanceof HourlyPayment) {
            return hourlyPaymentMapper.toDTO((HourlyPayment) payment);
        }

        if (payment instanceof RecurringPayment) {
            return recurringPaymentMapper.toDTO((RecurringPayment) payment);
        }

        throw new ApiException("Tipo de pagamento desconhecido.", HttpStatus.NOT_FOUND);
    }

    public Object findById(UUID id){
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Pagamento não encontrado para o ID: " + id, HttpStatus.NOT_FOUND));

        if (payment instanceof HourlyPayment) {
            return getHourlyPaymentById(id);
        }

        if (payment instanceof RecurringPayment) {
            return getRecurringPaymentById(id);
        }

        return new ApiException("Pagamento não encontrado.", HttpStatus.NOT_FOUND);
    }

    @Transactional
    public void deletePayment(UUID id){
        paymentRepository.deleteById(id);
    }

    public Object findUnpaidPayment(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veículo não encontrado para a placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if (vehicle.getAssociatedCustomer() == null) {
            HourlyPayment hourlyPayment = hourlyPaymentRepository
                    .findByPayerVehicleVehiclePlateAndStatus(vehiclePlate, PaymentStatus.PENDING);

            if (hourlyPayment == null) {
                hourlyPayment = hourlyPaymentRepository.findByPayerVehicleVehiclePlateAndStatus(vehiclePlate, PaymentStatus.PARTIAL);
            }

            if (hourlyPayment == null){
                throw new ApiException("Nenhum pagamento pendente encontrado para a placa: " + vehiclePlate, HttpStatus.NOT_FOUND);
            }

            return findById(hourlyPayment.getIdPayment());
        }

        RecurringPayment recurringPayment = recurringPaymentRepository
                .findByPayerCustomerCpfAndStatus(vehicle.getAssociatedCustomer().getCpf(), PaymentStatus.PENDING);

        if (recurringPayment == null) {
            recurringPayment = recurringPaymentRepository.findByPayerCustomerCpfAndStatus(vehicle.getAssociatedCustomer().getCpf(), PaymentStatus.PARTIAL);
        }

        if (recurringPayment == null){
            throw new ApiException("Nenhum pagamento pendente encontrado para o CPF: " + vehicle.getAssociatedCustomer().getCpf(), HttpStatus.NOT_FOUND);
        }

        return findById(recurringPayment.getIdPayment());
    }

    public Object handlePayment(String vehiclePlate, BigDecimal amount, String method) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if(vehicle.getEntryTime() == null){
            throw new ApiException("Veículo não está estacionado.", HttpStatus.NOT_FOUND);
        }

        if(Objects.equals(method, PaymentMethod.UNDEFINED.getValue())){
            throw new ApiException("Método de pagamento inválido.", HttpStatus.BAD_REQUEST);
        }

        Object payment = findUnpaidPayment(vehiclePlate);

        if (payment instanceof HourlyPaymentDTO hourlyPaymentDTO) {
            HourlyPayment hourlyPayment = hourlyPaymentMapper.toEntity(hourlyPaymentDTO);

            if(hourlyPayment.getType() == HourlyPaymentType.HOURLY){
                BigDecimal additionalAmount = calculateAmountToPay(vehicle.getEntryTime());
                hourlyPayment.setAmountToPay(hourlyPayment.getAmountToPay().add(additionalAmount));
            }

            processHourlyPayment(hourlyPayment, amount, method);
            hourlyPayment.setPayerVehicle(vehicle);
            return hourlyPaymentMapper.toDTO(hourlyPaymentRepository.saveAndFlush(hourlyPayment));
        }

        if (payment instanceof RecurringPaymentDTO recurringPaymentDTO) {
            Customer customer = customerRepository.findByCpf(vehicle.getAssociatedCustomer().getCpf())
                    .orElseThrow(() -> new ApiException("Cliente não encontrado para CPF: " + vehicle.getAssociatedCustomer().getCpf(), HttpStatus.NOT_FOUND));

            RecurringPayment recurringPayment = recurringPaymentMapper.toEntity(recurringPaymentDTO);
            processRecurringPayment(recurringPayment, amount, method);
            recurringPayment.setPayerCustomer(customer);
            return recurringPaymentMapper.toDTO(recurringPaymentRepository.saveAndFlush(recurringPayment));
        }

        throw new ApiException("Erro ao processar pagamento!", HttpStatus.NOT_ACCEPTABLE);
    }



    private void processHourlyPayment(HourlyPayment hourlyPayment, BigDecimal amount, String method) {
        validatePaymentAmount(hourlyPayment, amount);

        BigDecimal newPaidAmount = hourlyPayment.getPaidAmount().add(amount);
        hourlyPayment.setPaidAmount(newPaidAmount);
        hourlyPayment.setMethod(PaymentMethod.valueOf(method));

        updatePaymentStatus(hourlyPayment);

        if (hourlyPayment.getStatus() == PaymentStatus.PAID) {
            hourlyPayment.setPaymentDate(LocalDateTime.now());
        }
    }

    private void processRecurringPayment(RecurringPayment recurringPayment, BigDecimal amount, String method) {
        validateRecurringPaymentPeriod(recurringPayment);
        validatePaymentAmount(recurringPayment, amount);

        LocalDateTime now = LocalDateTime.now();
        BigDecimal newPaidAmount = recurringPayment.getPaidAmount().add(amount);
        recurringPayment.setPaidAmount(newPaidAmount);
        recurringPayment.setMethod(PaymentMethod.valueOf(method));

        updatePaymentStatus(recurringPayment);

        if (recurringPayment.getStatus() == PaymentStatus.PAID) {
            recurringPayment.setPaymentDate(now);
            
            if (recurringPayment.getPeriodStart() == null) {
                recurringPayment.setPeriodStart(now);
                recurringPayment.setPeriodEnd(now.plusMonths(1));
            }
            recurringPayment.setActive(true);
        }
    }

    private void validatePaymentAmount(Payment payment, BigDecimal amount) {
        BigDecimal remainingAmount = payment.getAmountToPay().subtract(payment.getPaidAmount());
        if (amount.compareTo(remainingAmount) > 0) {
            throw new ApiException(
                    String.format("Valor excede o saldo restante. Valor máximo permitido: %s", remainingAmount),
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Valor do pagamento deve ser maior que zero", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void validateRecurringPaymentPeriod(RecurringPayment recurringPayment) {
        if (recurringPayment.getPeriodEnd() != null && LocalDateTime.now().isAfter(recurringPayment.getPeriodEnd())) {
            recurringPayment.setStatus(PaymentStatus.OVERDUE);
            recurringPayment.setActive(false);
            throw new ApiException("Pagamento vencido. Gere um novo pagamento.", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private void updatePaymentStatus(Payment payment) {
        BigDecimal totalPaid = payment.getPaidAmount();
        BigDecimal totalToPay = payment.getAmountToPay();

        int comparison = totalPaid.compareTo(totalToPay);

        if (comparison < 0) {
            payment.setStatus(PaymentStatus.PARTIAL);
        } else if (comparison == 0) {
            payment.setStatus(PaymentStatus.PAID);
        }
    }

    private BigDecimal calculateAmountToPay(LocalDateTime entryTime) {
        long minutes = Duration.between(entryTime, LocalDateTime.now()).toMinutes();

        // Calcula as horas adicionais após a primeira hora paga
        long additionalHours = (minutes > 60) ? ((minutes - 60) / 5) : 0;

        return BigDecimal.valueOf(additionalHours).multiply(BigDecimal.valueOf(2.00));
    }

    public Object updateAmountToPay(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if(vehicle.getEntryTime() == null){
            throw new ApiException("Veículo não está estacionado.", HttpStatus.NOT_FOUND);
        }

        Object payment = findUnpaidPayment(vehiclePlate);

        if(payment instanceof HourlyPaymentDTO hourlyPaymentDTO){
            HourlyPayment hourlyPayment = hourlyPaymentMapper.toEntity(hourlyPaymentDTO);
            if(hourlyPayment.getType() == HourlyPaymentType.HOURLY){
                BigDecimal additionalAmount = calculateAmountToPay(vehicle.getEntryTime());
                hourlyPayment.setAmountToPay(hourlyPayment.getAmountToPay().add(additionalAmount));
                hourlyPayment.setPayerVehicle(vehicle);
                return hourlyPaymentMapper.toDTO(hourlyPaymentRepository.saveAndFlush(hourlyPayment));
            }
        }

        return payment;
    }

}
