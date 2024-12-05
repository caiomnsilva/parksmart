package com.api.park_management.services;

import com.api.park_management.enums.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.api.park_management.dto.CustomerDTO;
import com.api.park_management.dto.mapper.CustomerMapper;
import com.api.park_management.exceptions.ApiException;
import com.api.park_management.models.Customer;
import com.api.park_management.repositories.CustomerRepository;
import com.api.park_management.services.CustomerService;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdCustomer(UUID.randomUUID());
        customer.setCpf("23027932004");
        customer.setName("John Doe");

        customerDTO = new CustomerDTO(
                customer.getIdCustomer(),
                customer.getCpf(),
                customer.getName(),
                "981323412",
                "VIP",
                null,
                null
        );
    }

    @Test
    void testCreateCustomer() {
        when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        assertNotNull(createdCustomer);
        assertEquals("23027932004", createdCustomer.cpf());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testFindCustomerByCpf() {
        when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        CustomerDTO foundCustomer = customerService.findByCpf("23027932004");

        assertNotNull(foundCustomer);
        assertEquals("23027932004", foundCustomer.cpf());
    }

    @Test
    void testDeleteCustomer() {
        when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));

        customerService.deleteCustomer("23027932004");

        verify(customerRepository, times(1)).delete(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));
        doNothing().when(customerMapper).updateCustomerFromDTO(any(CustomerDTO.class), any(Customer.class));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(customerDTO);

        CustomerDTO updatedCustomer = customerService.updateCustomer("23027932004", customerDTO);

        assertNotNull(updatedCustomer);
        assertEquals("23027932004", updatedCustomer.cpf());
    }

    @Test
    void testUpdateCustomerWithError() {
        String invalidCpf = "INVALID123";

        when(customerRepository.findByCpf(invalidCpf))
                .thenThrow(new ApiException("Cliente não encontrado para cpf: " + invalidCpf, HttpStatus.NOT_FOUND));

        ApiException thrown = assertThrows(
                ApiException.class,
                () -> customerService.updateCustomer(invalidCpf, customerDTO),
                "Esperado ApiException ao tentar atualizar um cliente com CPF inválido"
        );

        assertEquals("Cliente não encontrado para cpf: " + invalidCpf, thrown.getMessage());
    }
}