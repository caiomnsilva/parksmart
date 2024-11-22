package com.api.park_management.dtos;

import com.api.park_management.models.enums.VehicleType;

public record VehicleRecordDto(String vehiclePlate,
                               String model,
                               String color,
                               String type) {
}
