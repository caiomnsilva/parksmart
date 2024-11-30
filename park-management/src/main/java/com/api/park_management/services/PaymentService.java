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

    private Object findUnpaidPayment(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veículo não encontrado para a placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if (vehicle.getAssociatedCustomer() == null) {
            HourlyPayment hourlyPayment = hourlyPaymentRepository
                    .findByPayerVehicleVehiclePlateAndStatus(vehiclePlate, PaymentStatus.PENDING);

            if (hourlyPayment == null) {
                throw new ApiException("Nenhum pagamento pendente encontrado para a placa: " + vehiclePlate, HttpStatus.NOT_FOUND);
            }

            return findById(hourlyPayment.getIdPayment());
        }

        RecurringPayment recurringPayment = recurringPaymentRepository
                .findByPayerCustomerCpfAndStatus(vehicle.getAssociatedCustomer().getCpf(), PaymentStatus.PENDING);

        if (recurringPayment == null) {
            throw new ApiException("Nenhum pagamento pendente encontrado para o cliente associado à placa: " + vehiclePlate, HttpStatus.NOT_FOUND);
        }

        return findById(recurringPayment.getIdPayment());
    }

    public Object payPayment(String vehiclePlate, BigDecimal amount, String method) {
        Object payment = findUnpaidPayment(vehiclePlate);

        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));

        if (payment instanceof HourlyPaymentDTO hourlyPaymentDTO) {
            HourlyPayment hourlyPayment = hourlyPaymentMapper.toEntity(hourlyPaymentDTO);
            processHourlyPayment(hourlyPayment, amount, method);
            hourlyPayment.setPayerVehicle(vehicle);
            return hourlyPaymentMapper.toDTO(hourlyPaymentRepository.save(hourlyPayment));
        }

        if (payment instanceof RecurringPaymentDTO recurringPaymentDTO) {

            Customer customer = customerRepository.findByCpf(vehicle.getAssociatedCustomer().getCpf())
                    .orElseThrow(() -> new ApiException("Cliente não encontrado para CPF: " + vehicle.getAssociatedCustomer().getCpf(), HttpStatus.NOT_FOUND));

            RecurringPayment recurringPayment = recurringPaymentMapper.toEntity(recurringPaymentDTO);
            processRecurringPayment(recurringPayment, amount, method);
            recurringPayment.setPayerCustomer(customer);
            return recurringPaymentMapper.toDTO(recurringPaymentRepository.save(recurringPayment));
        }

        throw new ApiException("Erro ao processar pagamento!", HttpStatus.NOT_ACCEPTABLE);
    }

    private void processHourlyPayment(HourlyPayment hourlyPayment, BigDecimal amount, String method) {
        hourlyPayment.setPaidAmount(amount);
        hourlyPayment.setMethod(PaymentMethod.valueOf(method));
        setPaymentStatus(hourlyPayment);

        if (hourlyPayment.getStatus() == PaymentStatus.PAID) {
            hourlyPayment.setPaymentDate(LocalDateTime.now());
        }

        hourlyPaymentRepository.saveAndFlush(hourlyPayment);
    }

    private void processRecurringPayment(RecurringPayment recurringPayment, BigDecimal amount, String method) {
        recurringPayment.setPaidAmount(amount);
        recurringPayment.setMethod(PaymentMethod.valueOf(method));
        setPaymentStatus(recurringPayment);

        if(recurringPayment.getStatus() == PaymentStatus.PAID){
            recurringPayment.setPaymentDate(LocalDateTime.now());
            recurringPayment.setPeriodStart(LocalDateTime.now());
            recurringPayment.setPeriodEnd(recurringPayment.getPeriodStart().plusMonths(1));
        }

        recurringPaymentRepository.saveAndFlush(recurringPayment);
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

    private BigDecimal calculateAmountToPay(LocalDateTime entryTime) {
        long minutes = Duration.between(entryTime, LocalDateTime.now()).toMinutes();

        /*
        Por motivos didaticos, o calculo do valor a ser pago ficará em 5 minutos representando 1 hora.
        A baixo o calculo correto para o valor a ser pago:
        long additionalHours = (minutes > 60) ? ((minutes - 1) / 60) + 1 : 0;
        */

        // Arredonda para a próxima "hora cheia" (5 minutos = 1 hora)
        long additionalHours = (minutes > 5) ? ((minutes - 1) / 5) + 1 : 0;

        BigDecimal additionalCost = BigDecimal.valueOf(additionalHours).multiply(BigDecimal.valueOf(2.00));

        return BigDecimal.valueOf(2.00).add(additionalCost);
    }

}
