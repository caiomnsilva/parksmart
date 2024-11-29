package com.api.park_management.dto.mapper;

import com.api.park_management.models.Vehicle;
import org.mapstruct.Mapper;

import com.api.park_management.dto.HourlyPaymentDTO;
import com.api.park_management.models.payment.HourlyPayment;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface HourlyPaymentMapper {
    @Mapping(source = "payerVehicle.vehiclePlate", target = "vehiclePlate")
    HourlyPaymentDTO toDTO(HourlyPayment payment);

    @Mapping(target = "payerVehicle", source = "vehiclePlate", qualifiedByName = "mapVehicle")
    HourlyPayment toEntity(HourlyPaymentDTO dto);

    @Named("mapVehicle")
    default Vehicle mapVehicle(String vehiclePlate) {
        if (vehiclePlate == null) return null;
        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(vehiclePlate);
        return vehicle;
    }
}
