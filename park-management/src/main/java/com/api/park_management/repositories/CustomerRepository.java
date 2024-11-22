package com.api.park_management.repositories;

import com.api.park_management.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Customer findByName(String name);

    Customer findByCpf(String cpf);
}