package com.api.park_management.services;

import com.api.park_management.dto.CustomerDTO;
import com.api.park_management.dto.mapper.CustomerMapper;
import com.api.park_management.models.Customer;
import com.api.park_management.models.Vehicle;
import com.api.park_management.repositories.CustomerRepository;
import com.api.park_management.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final VehicleRepository vehicleRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, VehicleRepository vehicleRepository) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.vehicleRepository = vehicleRepository;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO){
        return customerMapper.toDTO(customerRepository.save(customerMapper.toEntity(customerDTO)));
    }

    public CustomerDTO findByCpf(String cpf) {
        return customerMapper.toDTO(customerRepository.findByCpf(cpf));
    }

    @Transactional
    public void deleteCustomer(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf);
        customerRepository.delete(customer);
    }

    @Transactional
    public CustomerDTO updateCustomer(String cpf, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findByCpf(cpf);
        customerMapper.updateCustomerFromDTO(customerDTO, customer);
        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO addVehicle(String cpf, String vehiclePlate){
        Customer customer = customerRepository.findByCpf(cpf);
        Vehicle vehicle = vehicleRepository.findByVehiclePlateAndAssociatedCustomerIsNull(vehiclePlate);

        customer.getVehicles().add(vehicle);

        vehicle.setAssociatedCustomer(customer);
        vehicleRepository.save(vehicle);

        return customerMapper.toDTO(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDTO removeVehicle(String cpf, String vehiclePlate){
        Customer customer = customerRepository.findByCpf(cpf);
        Vehicle vehicle = vehicleRepository.findByVehiclePlate(vehiclePlate);

        if (!vehicle.getAssociatedCustomer().getCpf().equals(customer.getCpf())){
            throw new IllegalArgumentException("Vehicle is not associated with this customer");
        }

        customer.getVehicles().remove(vehicle);

        vehicle.setAssociatedCustomer(null);
        vehicleRepository.save(vehicle);

        return customerMapper.toDTO(customerRepository.save(customer));
    }
}
