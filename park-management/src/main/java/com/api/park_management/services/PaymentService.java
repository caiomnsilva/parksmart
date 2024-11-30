package com.api.park_management.services;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.dto.mapper.HourlyPaymentMapper;
import com.api.park_management.dto.mapper.RecurringPaymentMapper;
import com.api.park_management.enums.HourlyPaymentType;
import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.exceptions.ApiException;
import com.api.park_management.factory.PaymentFactory;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.HourlyPaymentRepository;
import com.api.park_management.repositories.PaymentRepository;
import com.api.park_management.repositories.RecurringPaymentRepository;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public PaymentService(PaymentRepository paymentRepository, HourlyPaymentMapper hourlyPaymentMapper, RecurringPaymentMapper recurringPaymentMapper, PaymentFactory paymentFactory, VehicleRepository vehicleRepository, HourlyPaymentRepository hourlyPaymentRepository, RecurringPaymentRepository recurringPaymentRepository) {
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentMapper = hourlyPaymentMapper;
        this.recurringPaymentMapper = recurringPaymentMapper;
        this.paymentFactory = paymentFactory;
        this.vehicleRepository = vehicleRepository;
        this.hourlyPaymentRepository = hourlyPaymentRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
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
        return findById(paymentFactory.createAndAssociatePayment(vehicle, type));
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

    private Object findUnpaidPayment(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if(vehicle.getAssociatedCustomer() == null) {
            HourlyPayment hourlyPayment = hourlyPaymentRepository
                    .findByPayerVehicleVehiclePlateAndStatus(vehiclePlate, PaymentStatus.PENDING);
            return findById(hourlyPayment.getIdPayment());
        }

        RecurringPayment recurringPayment = recurringPaymentRepository
                .findByPayerCustomerCpfAndStatus(vehicle.getAssociatedCustomer().getCpf(), PaymentStatus.PENDING);
        return findById(recurringPayment.getIdPayment());
    }

    public Object payPayment(String vehiclePlate, BigDecimal amount) {
        Object payment = findUnpaidPayment(vehiclePlate);

        if (payment instanceof HourlyPaymentDTO hourlyPaymentDTO) {
           payment = hourlyPaymentMapper.toEntity(hourlyPaymentDTO);
           ((HourlyPayment) payment).setPaidAmount(amount);
           setPaymentStatus((HourlyPayment) payment);
           ((HourlyPayment) payment).setPaymentDate(LocalDateTime.now());

           return hourlyPaymentMapper.toDTO(hourlyPaymentRepository.save((HourlyPayment) payment));
        }

        if (payment instanceof RecurringPaymentDTO recurringPaymentDTO) {
           payment = recurringPaymentMapper.toEntity(recurringPaymentDTO);
           ((RecurringPayment) payment).setPaidAmount(amount);
           setPaymentStatus((RecurringPayment) payment);
           ((RecurringPayment) payment).setPaymentDate(LocalDateTime.now());
           return recurringPaymentMapper.toDTO(recurringPaymentRepository.save((RecurringPayment) payment));
        }

        throw new ApiException("Erro ao processar pagamento!", HttpStatus.NOT_ACCEPTABLE);
    }

    private void setPaymentStatus(Payment payment){

        if(payment.getPaidAmount().compareTo(payment.getAmountToPay()) < 0){
            payment.setStatus(PaymentStatus.PARTIAL);
        }

        if(payment.getPaidAmount().compareTo(payment.getAmountToPay()) == 0){
            payment.setStatus(PaymentStatus.PAID);
        }

        if (payment.getPaidAmount().compareTo(payment.getAmountToPay()) > 0){
            throw new ApiException("Valor pago maior que o valor a ser pago!", HttpStatus.NOT_ACCEPTABLE);
        }

        if (payment instanceof RecurringPayment recurringPayment && LocalDateTime.now().isAfter(recurringPayment.getPeriodEnd())) {
            payment.setStatus(PaymentStatus.OVERDUE);
            throw new ApiException("Pagamento em vencido. Gere um novo pagamento.", HttpStatus.NOT_ACCEPTABLE);
        }

    }

}
