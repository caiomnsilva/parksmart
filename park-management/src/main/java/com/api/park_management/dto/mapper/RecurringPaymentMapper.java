package com.api.park_management.dto.mapper;

import com.api.park_management.models.Customer;
import org.mapstruct.Mapper;

import com.api.park_management.dto.RecurringPaymentDTO;
import com.api.park_management.models.payment.RecurringPayment;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RecurringPaymentMapper {

    @Mapping(source = "payerCustomer.cpf", target = "customerCpf")
    RecurringPaymentDTO toDTO(RecurringPayment recurringPayment);

    @Mapping(target = "payerCustomer", source = "customerCpf", qualifiedByName = "mapCustomer")
    RecurringPayment toEntity(RecurringPaymentDTO recurringPaymentDTO);

    @Named("mapCustomer")
    default Customer mapCustomer(String customerCpf) {
        if (customerCpf == null) return null;
        Customer customer = new Customer();
        customer.setCpf(customerCpf);
        return customer;
    }
}
