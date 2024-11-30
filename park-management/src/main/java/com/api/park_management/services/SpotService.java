package com.api.park_management.services;

import java.util.List;
import java.util.stream.Collectors;

import com.api.park_management.enums.CustomerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.park_management.dto.SpotDTO;
import com.api.park_management.dto.mapper.SpotMapper;
import com.api.park_management.enums.SpotType;
import com.api.park_management.models.Spot;
import com.api.park_management.models.Vehicle;
import com.api.park_management.repositories.SpotRepository;
import com.api.park_management.repositories.VehicleRepository;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final VehicleRepository vehicleRepository;
    private final SpotMapper spotMapper;

    public SpotService(SpotRepository spotRepository, VehicleRepository vehicleRepository, SpotMapper spotMapper) {
        this.spotRepository = spotRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotMapper = spotMapper;
    }

    public List<SpotDTO> getAllSpots() {
        return spotRepository.findAll()
                             .stream()
                             .map(spotMapper::toDTO)
                             .collect(Collectors.toList());
    }

    public SpotDTO findBySpotNumber(int spotNumber){
        return spotMapper.toDTO(spotRepository.findBySpotNumber(spotNumber));
    }

    @Transactional
    public SpotDTO saveSpot(SpotDTO spotDTO){
        return spotMapper.toDTO(spotRepository.save(spotMapper.toEntity(spotDTO)));
    }

    @Transactional
    public SpotDTO updateSpot(int spotNumber, SpotDTO spotDTO){
        Spot spot = spotRepository.findBySpotNumberAndCurrentVehicleIsNull(spotNumber);
        spotMapper.updateSpotFromDTO(spotDTO, spot);
        return spotMapper.toDTO(spotRepository.save(spot));
    }

    @Transactional
    public void deleteSpot(int spotNumber){
        Spot spot = spotRepository.findBySpotNumberAndCurrentVehicleIsNull(spotNumber);
        spotRepository.delete(spot);
    }

    @Transactional
    public SpotDTO parkVehicle(String vehiclePlate){
        Vehicle vehicle = vehicleRepository.findByVehiclePlateAndCurrentSpotIsNull(vehiclePlate);

        //define o tipo da vaga de acordo com o tipo do veículo ou tipo de cliente associado ao veículo.
        SpotType spotType = (vehicle.getAssociatedCustomer() == null || vehicle.getAssociatedCustomer().getType() != CustomerType.MONTHLY)
                ? SpotType.fromValue(vehicle.getType().getValue())
                : SpotType.fromValue(vehicle.getAssociatedCustomer().getType().getValue());

        Spot spot = spotRepository.findFirstByTypeAndOccupiedFalse(spotType);

        spot.setCurrentVehicle(vehicle);
        spot.setOccupied(true);

        spotRepository.save(spot);

        return spotMapper.toDTO(spot);
    }

    @Transactional
    public SpotDTO unparkVehicle(String vehiclePlate){
        Spot spot = spotRepository.findByCurrentVehicleVehiclePlate(vehiclePlate);

        spot.setCurrentVehicle(null);
        spot.setOccupied(false);

        spotRepository.save(spot);

        return spotMapper.toDTO(spot);
    }

    public SpotDTO findByVehiclePlate(String vehiclePlate){
        return spotMapper.toDTO(spotRepository.findByCurrentVehicleVehiclePlate(vehiclePlate));
    }

}
