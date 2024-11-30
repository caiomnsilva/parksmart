package com.api.park_management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Length;

import com.api.park_management.enums.VehicleType;
import com.api.park_management.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VehicleDTO(
        @JsonProperty("_id") UUID idVehicle,

        @NotBlank @Length(min = 7, max = 7) @Pattern(regexp="^[A-Z]{3}\\d[A-Z\\d]\\d{2}") String vehiclePlate,

        @NotBlank @Length(min = 3, max = 25) String model,

        @NotBlank @Length(min = 3, max = 25) String color,

        @ValueOfEnum(enumClass = VehicleType.class) String type,

        @PastOrPresent LocalDateTime entryTime,

        String customerCpf,

        Integer spotNumber
        ) {
}
