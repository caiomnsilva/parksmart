package com.api.park_management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.park_management.models.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
