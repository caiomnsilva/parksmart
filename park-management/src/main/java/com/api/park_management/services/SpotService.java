package com.api.park_management.services;

import com.api.park_management.dtos.SpotRecordDto;
import com.api.park_management.models.Spot;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.enums.SpotType;
import com.api.park_management.repositories.SpotRepository;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;

    public SpotService(SpotRepository spotRepository, VehicleRepository vehicleRepository) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    public Spot findBySpotNumber(int spotNumber){
        return spotRepository.findBySpotNumber(spotNumber);
    }

    @Transactional
    public Spot saveSpot(SpotRecordDto spotRecordDto){
        Spot spot = new Spot();
        spot.setSpotNumber(spotRecordDto.spotNumber());
        spot.setType(SpotType.valueOf(spotRecordDto.type()));

        return spotRepository.save(spot);
    }

    @Transactional
    public Spot putVehicleInSpot(int spotNumber, String vehiclePlate){
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate);
        Spot spot = findBySpotNumber(spotNumber);

        spot.setCurrentVehicle(vehicle);
        spot.setOcuppied(true);

        return spotRepository.save(spot);
    }

}
