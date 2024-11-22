package com.api.park_management.repositories;

import com.api.park_management.models.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpotRepository extends JpaRepository<Spot, UUID> {
    Spot findBySpotNumber(int spotNumber);
}
