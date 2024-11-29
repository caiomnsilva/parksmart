package com.api.park_management.dto.mapper;

import org.mapstruct.*;

import com.api.park_management.dto.SpotDTO;
import com.api.park_management.models.Spot;
import com.api.park_management.models.Vehicle;

@Mapper(componentModel = "spring")
public interface SpotMapper {

    @Mapping(source = "currentVehicle.vehiclePlate", target = "vehiclePlate")
    SpotDTO toDTO(Spot spot);

    @Mapping(target = "currentVehicle", source = "vehiclePlate", qualifiedByName = "mapVehicle")
    Spot toEntity(SpotDTO spotDTO);

    @Named("mapVehicle")
    default Vehicle mapVehicle(String vehiclePlate) {
        if (vehiclePlate == null) return null;
        Vehicle vehicle = new Vehicle();
        vehicle.setVehiclePlate(vehiclePlate);
        return vehicle;
    }

    @Mapping(target = "currentVehicle", source = "vehiclePlate", qualifiedByName = "mapVehicle")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSpotFromDTO(SpotDTO spotDTO, @MappingTarget Spot spot);
}
