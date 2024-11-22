package com.api.park_management.dtos;

import com.api.park_management.models.Vehicle;
import com.api.park_management.models.enums.CustomerType;

import java.util.Set;

public record CustomerRecordDto(String cpf,
                                String name,
                                String phone,
                                String type) {
}
