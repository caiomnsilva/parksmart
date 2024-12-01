package com.api.park_management.services;

import com.api.park_management.dto.VehicleDTO;
import com.api.park_management.enums.HourlyPaymentType;
import com.api.park_management.exceptions.ApiException;
import com.api.park_management.factory.PaymentFactory;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.Payment;
import com.api.park_management.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ParkingService {
    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final HourlyPaymentRepository hourlyPaymentRepository;
    private final RecurringPaymentRepository recurringPaymentRepository;
    private final SpotService spotService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final PaymentFactory paymentFactory;
    public ParkingService(SpotRepository spotRepository, VehicleRepository vehicleRepository, CustomerRepository customerRepository, PaymentRepository paymentRepository, HourlyPaymentRepository hourlyPaymentRepository, RecurringPaymentRepository recurringPaymentRepository, SpotService spotService, VehicleService vehicleService, CustomerService customerService, PaymentService paymentService, PaymentFactory paymentFactory) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.hourlyPaymentRepository = hourlyPaymentRepository;
        this.recurringPaymentRepository = recurringPaymentRepository;
        this.spotService = spotService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.paymentService = paymentService;
        this.paymentFactory = paymentFactory;
    }

    @Transactional
    public void handleEntry(String vehiclePlate, String type) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlateAndCurrentSpotIsNull(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veículo não encontrado ou já está dentro do estacionamento!", HttpStatus.NOT_FOUND));

        vehicle.setEntryTime(LocalDateTime.now());
        paymentFactory.createAndAssociatePayment(vehicle, type);
        spotService.parkVehicle(vehiclePlate);

        vehicleRepository.saveAndFlush(vehicle);

    }

    @Transactional
    public Object handleExit(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlateAndCurrentSpotIsNotNull(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veículo não encontrado ou não está dentro do estacionamento!", HttpStatus.NOT_FOUND));

        Object payment = paymentService.findUnpaidPayment(vehiclePlate);

        if(payment == null){
        spotService.unparkVehicle(vehiclePlate);
        vehicle.setEntryTime(null);
        vehicleRepository.saveAndFlush(vehicle);

        }

        throw new ApiException("Pagamento não encontrado!", HttpStatus.NOT_FOUND);
    }
}
