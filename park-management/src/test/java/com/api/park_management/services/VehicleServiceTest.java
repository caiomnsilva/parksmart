package com.api.park_management.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import com.api.park_management.exceptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.park_management.dto.VehicleDTO;
import com.api.park_management.dto.mapper.VehicleMapper;
import com.api.park_management.models.Vehicle;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setIdVehicle(UUID.randomUUID());
        vehicle.setVehiclePlate("ABC1234");
        vehicle.setModel("Model X");
        vehicle.setColor("Red");

        vehicleDTO = new VehicleDTO(
                vehicle.getIdVehicle(),
                vehicle.getVehiclePlate(),
                vehicle.getModel(),
                vehicle.getColor(),
                "CAR",
                null,
                null,
                null
        );
    }

    @Test
    void testCreateVehicle() {
        when(vehicleMapper.toEntity(any(VehicleDTO.class))).thenReturn(vehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(vehicleDTO);

        VehicleDTO createdVehicle = vehicleService.createVehicle(vehicleDTO);

        assertNotNull(createdVehicle);
        assertEquals("ABC1234", createdVehicle.vehiclePlate());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testFindVehicleByPlate() {
        when(vehicleRepository.findByVehiclePlate(anyString())).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(vehicleDTO);

        VehicleDTO foundVehicle = vehicleService.findVehicleByPlate("ABC1234");

        assertNotNull(foundVehicle);
        assertEquals("ABC1234", foundVehicle.vehiclePlate());
    }

    @Test
    void testUpdateVehicle() {
        when(vehicleRepository.findByVehiclePlate(anyString())).thenReturn(Optional.of(vehicle));
        doNothing().when(vehicleMapper).updateVehicleFromDTO(any(VehicleDTO.class), any(Vehicle.class));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
        when(vehicleMapper.toDTO(any(Vehicle.class))).thenReturn(vehicleDTO);

        VehicleDTO updatedVehicle = vehicleService.updateVehicle("ABC1234", vehicleDTO);

        assertNotNull(updatedVehicle);
        assertEquals("ABC1234", updatedVehicle.vehiclePlate());
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.findByVehiclePlateAndCurrentSpotIsNullAndAssociatedCustomerIsNull(anyString()))
                .thenReturn(vehicle);

        vehicleService.deleteVehicle("ABC1234");

        verify(vehicleRepository, times(1)).delete(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicleWithError() {
        String invalidPlate = "INVALID123";

        when(vehicleRepository.findByVehiclePlate(invalidPlate))
                .thenThrow(new ApiException("Veiculo não encontrado para placa: " + invalidPlate, HttpStatus.NOT_FOUND));

        ApiException thrown = assertThrows(
                ApiException.class,
                () -> vehicleService.updateVehicle(invalidPlate, vehicleDTO),
                "Esperado ApiException ao tentar atualizar um veículo com placa inválida"
        );

        assertEquals("Veiculo não encontrado para placa: " + invalidPlate, thrown.getMessage());
    }
}