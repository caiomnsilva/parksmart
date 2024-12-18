package com.api.park_management.controllers;

import com.api.park_management.dto.SpotDTO;
import com.api.park_management.enums.SpotType;
import com.api.park_management.services.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/park-smart/spots")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @Operation(summary = "Lista todos as vagas.", description = "Endpoint para listar todas as vagas.")
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<SpotDTO> getAllSpots() {
        return spotService.getAllSpots();
    }

    @Operation(summary = "Acha uma vaga pelo numero.", description = "Endpoint para achar uma vaga pelo numero.")
    @GetMapping("number/{spotNumber}")
    @ResponseStatus(code = HttpStatus.OK)
    public SpotDTO getBySpotNumber(@PathVariable int spotNumber){
        return spotService.findBySpotNumber(spotNumber);
    }

    @Operation(summary = "Acha uma vaga pela placa do veiculo.", description = "Endpoint para achar uma vaga pela placa do veiculo.")
    @GetMapping("plate/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public SpotDTO getByVehiclePlate(@PathVariable String vehiclePlate){
        return spotService.findByVehiclePlate(vehiclePlate);
    }

    @Operation(summary = "Cadastrar nova vaga.", description = "Endpoint para cadastrar uma nova vaga.")
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public SpotDTO saveSpot(@RequestBody SpotDTO spotDTO){
        return spotService.saveSpot(spotDTO);
    }

    @Operation(summary = "Atualizar vaga.", description = "Endpoint para atualizar uma vaga.")
    @PutMapping("/{spotNumber}")
    @ResponseStatus(code = HttpStatus.OK)
    public SpotDTO updateSpot(@PathVariable int spotNumber, @RequestBody SpotDTO spotDTO){
        return spotService.updateSpot(spotNumber, spotDTO);
    }

    @Operation(summary = "Associa veiculo a vaga.", description = "Endpoint para adicionar um veiculo a uma vaga.")
    @PutMapping("/add/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public SpotDTO parkingVehicle(@PathVariable String vehiclePlate){
        return spotService.parkVehicle(vehiclePlate);
    }

    @Operation(summary = "Remove veiculo da vaga.", description = "Endpoint para remover um veiculo de uma vaga.")
    @PutMapping("/remove/{vehiclePlate}")
    @ResponseStatus(code = HttpStatus.OK)
    public SpotDTO putVehicleInSpot(@PathVariable String vehiclePlate){
        return spotService.unparkVehicle(vehiclePlate);
    }

    @Operation(summary = "Retorna o total de vagas.", description = "Endpoint para retornar o total de vagas.")
    @GetMapping("/total")
    @ResponseStatus(code = HttpStatus.OK)
    public long getTotalSpots() {
        return spotService.getTotalSpots();
    }

    @Operation(summary = "Retorna o total de vagas ocupadas.", description = "Endpoint para retornar o total de vagas ocupadas.")
    @GetMapping("/occupied")
    @ResponseStatus(code = HttpStatus.OK)
    public long getOccupiedSpots() {
        return spotService.getOccupiedSpots();
    }

    @Operation(summary = "Retorna o total de vagas livres.", description = "Endpoint para retornar o total de vagas livres.")
    @GetMapping("/available")
    @ResponseStatus(code = HttpStatus.OK)
    public long getAvailableSpots() {
        return spotService.getAvailableSpots();
    }

    @Operation(summary = "Retorna a porcentagem de ocupação.", description = "Endpoint para retornar a porcentagem de ocupação.")
    @GetMapping("/occupancy-rate")
    @ResponseStatus(code = HttpStatus.OK)
    public double getOccupancyRate() {
        return spotService.getOccupancyRate();
    }

    @Operation(summary = "Acha uma lista de vagas pelo tipo.", description = "Endpoint para achar uma lista de vagas pelo tipo.")
    @GetMapping("/type/{type}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<SpotDTO> getByType(@PathVariable String type){
        return spotService.getByType(SpotType.valueOf(type));
    }

    @Operation(summary = "Deleta uma vaga pelo numero.", description = "Endpoint para deletar uma vaga pelo numero.")
    @DeleteMapping("/{spotNumber}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteSpot(@PathVariable int spotNumber){
        spotService.deleteSpot(spotNumber);
    }
}
