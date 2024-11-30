package com.api.park_management.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.park_management.enums.SpotType;
import com.api.park_management.models.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, UUID> {
    Spot findBySpotNumber(int spotNumber);

    Optional<Spot> findFirstByTypeAndOccupiedFalse(SpotType type);

    Spot findBySpotNumberAndCurrentVehicleIsNull(int spotNumber);

   Optional<Spot> findByCurrentVehicleVehiclePlate(String vehiclePlate);

    List<Spot> findByType(SpotType type);

    @Query("SELECT COUNT(s) FROM Spot s")
    long countTotalSpots();

    @Query("SELECT COUNT(s) FROM Spot s WHERE s.occupied = true")
    long countOccupiedSpots();

    @Query("SELECT COUNT(s) FROM Spot s WHERE s.occupied = false")
    long countAvailableSpots();
}
