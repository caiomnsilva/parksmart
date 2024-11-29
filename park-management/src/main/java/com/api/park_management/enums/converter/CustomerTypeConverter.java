package com.api.park_management.enums.converter;

import jakarta.persistence.Converter;
import com.api.park_management.enums.CustomerType;

@Converter(autoApply = true)
public class CustomerTypeConverter extends GenericEnumConverter<CustomerType> {
    public CustomerTypeConverter() {
        super(CustomerType.class);
    }
}
