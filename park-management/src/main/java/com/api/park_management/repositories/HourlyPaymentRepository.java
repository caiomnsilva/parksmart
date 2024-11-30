package com.api.park_management.repositories;

import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.models.payment.HourlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HourlyPaymentRepository extends JpaRepository<HourlyPayment, UUID> {
    HourlyPayment findHourlyPaymentByPayerVehicleVehiclePlate(String vehiclePlate);

    List<HourlyPayment> findByPayerVehicleVehiclePlateAndStatusIn(String vehiclePlate, List<PaymentStatus> status);

    HourlyPayment findByPayerVehicleVehiclePlateAndStatus(String vehiclePlate, PaymentStatus status);
}
