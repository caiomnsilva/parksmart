package com.api.park_management.repositories;

import com.api.park_management.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByVehiclePlate(String vehiclePlate);
    
    Optional<Vehicle> findByVehiclePlateAndCurrentSpotIsNull(String vehiclePlate);

    Vehicle findByVehiclePlateAndAssociatedCustomerIsNull(String vehiclePlate);

    Vehicle findByVehiclePlateAndCurrentSpotIsNullAndAssociatedCustomerIsNull(String vehiclePlate);
}
