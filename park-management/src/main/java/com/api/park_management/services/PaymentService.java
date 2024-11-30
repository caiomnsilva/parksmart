package com.api.park_management.services;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.dto.mapper.HourlyPaymentMapper;
import com.api.park_management.dto.mapper.RecurringPaymentMapper;
import com.api.park_management.factory.PaymentFactory;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.models.payment.RecurringPayment;
import com.api.park_management.repositories.PaymentRepository;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PaymentService(PaymentRepository paymentRepository, HourlyPaymentMapper hourlyPaymentMapper, RecurringPaymentMapper recurringPaymentMapper, PaymentFactory paymentFactory, VehicleRepository vehicleRepository) {
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentMapper = hourlyPaymentMapper;
        this.recurringPaymentMapper = recurringPaymentMapper;
        this.paymentFactory = paymentFactory;
        this.vehicleRepository = vehicleRepository;
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
    public Object createAndAssociatePayment(String vehiclePlate){
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate);
        return findById(paymentFactory.createAndAssociatePayment(vehicle));
    }

    public Object findById(UUID id) throws RuntimeException {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        if (payment instanceof HourlyPayment) {
            return getHourlyPaymentById(id);
        }

        if (payment instanceof RecurringPayment) {
            return getRecurringPaymentById(id);
        }

        return new RuntimeException("Unknown payment type");
    }

    @Transactional
    public void deletePayment(UUID id){
        paymentRepository.deleteById(id);
    }
}
