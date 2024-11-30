package com.api.park_management.controllers;

import com.api.park_management.dto.CustomerDTO;
import com.api.park_management.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/park-smart/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Criar novo cliente", description = "Endpoint para criar um novo cliente.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @Operation(summary = "Buscar cliente por CPF", description = "Endpoint para buscar um cliente por CPF.")
    @GetMapping("/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomerByCpf(@PathVariable String cpf) {
        return customerService.findByCpf(cpf);
    }

    @Operation(summary = "Adicionar veículo ao cliente", description = "Endpoint para adicionar um veículo ao cliente.")
    @PatchMapping("/{cpf}/vehicles/{vehiclePlate}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO addVehicle(@PathVariable String cpf, @PathVariable String vehiclePlate) {
        return customerService.addVehicle(cpf, vehiclePlate);
    }

    @Operation(summary = "Remover veículo do cliente", description = "Endpoint para remover um veículo do cliente.")
    @PutMapping("/{cpf}/vehicles/{vehiclePlate}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO removeVehicle(@PathVariable String cpf, @PathVariable String vehiclePlate) {
        return customerService.removeVehicle(cpf, vehiclePlate);
    }

    @Operation(summary = "Listar todos os clientes", description = "Endpoint para listar todos os clientes.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Deletar cliente por CPF", description = "Endpoint para deletar um cliente por CPF.")
    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomerDTO deleteCustomer(@PathVariable String cpf) {
        customerService.deleteCustomer(cpf);
        return null;
    }

    @Operation(summary = "Atualizar cliente por CPF", description = "Endpoint para atualizar um cliente por CPF.")
    @PutMapping("/{cpf}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO updateCustomer(@PathVariable String cpf, @RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(cpf, customerDTO);
    }
}
