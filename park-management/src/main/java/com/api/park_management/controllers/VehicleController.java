package com.api.park_management.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.api.park_management.dto.VehicleDTO;
import com.api.park_management.services.VehicleService;

import io.swagger.v3.oas.annotations.Operation;

@Validated
@RestController
@RequestMapping("api/park-smart/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Lista todos os veiculos.", description = "Endpoint para listar todos os veiculos.")
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @Operation(summary = "Acha um veiculo pela placa.", description = "Endpoint para achar um veiculo pela placa.")
    @GetMapping("/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public VehicleDTO getByVehiclePlate(@PathVariable String vehiclePlate) {
        return vehicleService.findVehicleByPlate(vehiclePlate);
    }

    @Operation(summary = "Cadastrar novo veiculo.", description = "Endpoint para cadastrar um novo veiculo.")
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public VehicleDTO saveVehicle(@RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.createVehicle(vehicleDTO);
    }

    @Operation(summary = "Atualiza novo veiculo.", description = "Endpoint para atualizar um veiculo.")
    @PutMapping("/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public VehicleDTO updateVehicle(@PathVariable String vehiclePlate, @RequestBody VehicleDTO vehicleDTO) {
        return vehicleService.updateVehicle(vehiclePlate, vehicleDTO);
    }

    @Operation(summary = "Deleta um veiculo.", description = "Endpoint para deletar um veiculo.")
    @DeleteMapping("/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteVehicle(@PathVariable String vehiclePlate) {
        vehicleService.deleteVehicle(vehiclePlate);
    }
}
