package com.api.park_management.repositories;

import java.util.List;
import java.util.UUID;

import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.models.payment.HourlyPayment;
import com.api.park_management.models.payment.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.park_management.models.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("SELECT p FROM RecurringPayment p")
    List<RecurringPayment> findAllRecurringPayments();

    @Query("SELECT p FROM HourlyPayment p")
    List<HourlyPayment> findAllHourlyPayments();

    @Query("SELECT p FROM RecurringPayment p WHERE p.idPayment = :id")
    RecurringPayment findRecurringPaymentById(UUID id);

    @Query("SELECT p FROM HourlyPayment p WHERE p.idPayment = :id")
    HourlyPayment findHourlyPaymentById(UUID id);

    //List<Payment> findByVehiclePlateAndStatusIn(String vehiclePlate, List<PaymentStatus> status);

}
