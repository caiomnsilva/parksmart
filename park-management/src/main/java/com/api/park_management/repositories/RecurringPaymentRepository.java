package com.api.park_management.repositories;

import com.api.park_management.enums.PaymentStatus;
import com.api.park_management.models.payment.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, UUID> {
    RecurringPayment findRecurringPaymentByPayerCustomerCpf(String cpf);

    List<RecurringPayment> findByPayerCustomerCpfAndStatusIn(String cpf, List<PaymentStatus> status);

    RecurringPayment findByPayerCustomerCpfAndStatus(String cpf, PaymentStatus status);
}
