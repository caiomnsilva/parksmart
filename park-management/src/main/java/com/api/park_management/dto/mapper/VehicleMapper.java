package com.api.park_management.dto.mapper;

import com.api.park_management.models.Customer;
import com.api.park_management.models.Spot;
import org.mapstruct.*;

import com.api.park_management.dto.VehicleDTO;
import com.api.park_management.models.Vehicle;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(source = "associatedCustomer.cpf", target = "customerCpf")
    @Mapping(source = "currentSpot.spotNumber", target = "spotNumber")
    VehicleDTO toDTO(Vehicle vehicle);

    @Mapping(target = "associatedCustomer", source = "customerCpf", qualifiedByName = "mapCustomer")
    @Mapping(target = "currentSpot", source = "spotNumber", qualifiedByName = "mapSpot")
    Vehicle toEntity(VehicleDTO vehicleDTO);

    @Named("mapCustomer")
    default Customer mapCustomer(String customerCpf) {
        if (customerCpf == null) return null;
        Customer customer = new Customer();
        customer.setCpf(customerCpf);
        return customer;
    }

    @Named("mapSpot")
    default Spot mapSpot(Integer spotNumber) {
        if (spotNumber == null) return null;
        Spot spot = new Spot();
        spot.setSpotNumber(spotNumber);
        return spot;
    }

    @Mapping(target = "currentSpot", source = "spotNumber", qualifiedByName = "mapSpot")
    @Mapping(target = "associatedCustomer", source = "customerCpf", qualifiedByName = "mapCustomer")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVehicleFromDTO(VehicleDTO vehicleDTO, @MappingTarget Vehicle vehicle);
    
}
