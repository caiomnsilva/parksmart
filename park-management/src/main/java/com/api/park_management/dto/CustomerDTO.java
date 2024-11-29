package com.api.park_management.dto;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CustomerDTO(
    @JsonProperty("_id") UUID idCustomer,

    @CPF String cpf,

    @NotBlank @Size(min = 5, max = 100) String name,

    @Positive @Length(min=11, max=11)String phone,

    @NotBlank @ValueOfEnum(enumClass = CustomerType.class) String type,
    
    List<String> vehiclesPlates,

    List<UUID> payments
    
    ) {
}
