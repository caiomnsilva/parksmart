package com.api.park_management.dto;

import java.util.UUID;

import com.api.park_management.enums.SpotType;
import com.api.park_management.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SpotDTO(
    @JsonProperty("_id") UUID idSpot,
    @PositiveOrZero(message = "O número da vaga deve ser positivo.") @NotNull Integer spotNumber,
    @ValueOfEnum(enumClass = SpotType.class) String type,
    boolean occupied,
    String vehiclePlate
    ) {
}
