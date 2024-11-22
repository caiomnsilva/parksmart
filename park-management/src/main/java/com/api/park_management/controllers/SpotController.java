package com.api.park_management.controllers;

import com.api.park_management.dtos.SpotRecordDto;
import com.api.park_management.models.Spot;
import com.api.park_management.services.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/park-smart/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @Operation(summary = "Lista todos as vagas.", description = "Endpoint para listar todas as vagas.")
    @GetMapping
    public ResponseEntity<List<Spot>> getAllSpots() {
        return ResponseEntity.status(HttpStatus.OK).body(spotService.getAllSpots());
    }

    @Operation(summary = "Acha uma vaga pelo numero.", description = "Endpoint para achar uma vaga pelo numero.")
    @GetMapping("/{spotNumber}")
    public ResponseEntity<Spot> getBySpotNumber(@PathVariable int spotNumber){
        return ResponseEntity.status(HttpStatus.OK).body(spotService.findBySpotNumber(spotNumber));
    }

    @Operation(summary = "Cadastrar nova vaga.", description = "Endpoint para cadastrar uma nova vaga.")
    @PostMapping
    public ResponseEntity<Spot> saveSpot(@RequestBody SpotRecordDto spotRecordDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(spotService.saveSpot(spotRecordDto));
    }

    @Operation(summary = "Associar veiculo a vaga.", description = "Endpoint para veiculo a uma vaga.")
    @PatchMapping("/{spotNumber}/{vehiclePlate}")
    public ResponseEntity<Spot> putVehicleInSpot(@PathVariable int spotNumber, @PathVariable String vehiclePlate){
        return ResponseEntity.status(HttpStatus.OK).body(spotService.putVehicleInSpot(spotNumber, vehiclePlate));
    }
}
