package com.api.park_management.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.park_management.enums.SpotType;
import com.api.park_management.models.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, UUID> {
    Spot findBySpotNumber(int spotNumber);

    Spot findFirstByTypeAndOccupiedFalse(SpotType type);

    Spot findBySpotNumberAndCurrentVehicleIsNull(int spotNumber);

    Spot findByCurrentVehicleVehiclePlate(String vehiclePlate);
}
