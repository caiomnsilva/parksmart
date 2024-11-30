package com.api.park_management.services;

import java.util.List;
import java.util.stream.Collectors;

import com.api.park_management.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.api.park_management.dto.VehicleDTO;
import com.api.park_management.dto.mapper.VehicleMapper;
import com.api.park_management.models.Vehicle;
import com.api.park_management.repositories.VehicleRepository;

@Validated
@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public List<VehicleDTO> getAllVehicles(){
        return vehicleRepository.findAll()
                                .stream()
                                .map(vehicleMapper::toDTO)
                                .collect(Collectors.toList());
    }

    public VehicleDTO findVehicleByPlate(String vehiclePlate) {
        return vehicleMapper.toDTO(vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        return vehicleMapper.toDTO(vehicleRepository.save(vehicleMapper.toEntity(vehicleDTO)));
    }

    @Transactional
    public VehicleDTO updateVehicle(String vehiclePlate, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate)
                .orElseThrow(() -> new ApiException("Veiculo não encontrado para placa: " + vehiclePlate, HttpStatus.NOT_FOUND));
        vehicleMapper.updateVehicleFromDTO(vehicleDTO, vehicle);
        return vehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(String vehiclePlate) {
        Vehicle vehicle = vehicleRepository.findByVehiclePlateAndCurrentSpotIsNullAndAssociatedCustomerIsNull(vehiclePlate);
        vehicleRepository.delete(vehicle);
    }
}
