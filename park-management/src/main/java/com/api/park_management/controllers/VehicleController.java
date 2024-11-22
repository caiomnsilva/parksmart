package com.api.park_management.controllers;

import com.api.park_management.dtos.VehicleRecordDto;
import com.api.park_management.models.Vehicle;
import com.api.park_management.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/park-smart/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Lista todos os veiculos.", description = "Endpoint para listar todos os veiculos.")
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.getAllVehicles());
    }

    @Operation(summary = "Acha um veiculo pela placa.", description = "Endpoint para achar um veiculo pela placa.")
    @GetMapping("/{vehiclePlate}")
    public ResponseEntity<Vehicle> getByVehiclePlate(@PathVariable String vehiclePlate) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.findVehicleByPlate(vehiclePlate));
    }

    @Operation(summary = "Cadastrar novo veiculo.", description = "Endpoint para cadastrar um novo veiculo.")
    @PostMapping
    public ResponseEntity<Vehicle> saveVehicle(@RequestBody VehicleRecordDto vehicleRecordDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.saveVehicle(vehicleRecordDto));
    }
}
