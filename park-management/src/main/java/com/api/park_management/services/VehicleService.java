package com.api.park_management.services;

import com.api.park_management.dtos.VehicleRecordDto;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.enums.VehicleType;
import com.api.park_management.repositories.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles(){
        return vehicleRepository.findAll();
    }

    public Vehicle findVehicleByPlate(String vehiclePlate) {
        return vehicleRepository.findByVehiclePlate(vehiclePlate);
    }

    @Transactional
    public Vehicle saveVehicle(VehicleRecordDto vehicleRecordDto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(vehicleRecordDto.vehiclePlate());
        vehicle.setColor(vehicleRecordDto.color());
        vehicle.setModel(vehicleRecordDto.model());
        vehicle.setType(VehicleType.valueOf(vehicleRecordDto.type()));

        return vehicleRepository.save(vehicle);
    }
}
