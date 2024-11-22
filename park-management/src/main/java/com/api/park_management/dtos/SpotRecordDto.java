package com.api.park_management.dtos;

import com.api.park_management.models.enums.SpotType;

public record SpotRecordDto(int spotNumber,
                            String type) {
}
