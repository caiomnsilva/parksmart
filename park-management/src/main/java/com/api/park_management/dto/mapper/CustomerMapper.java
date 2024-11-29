package com.api.park_management.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.*;

import com.api.park_management.dto.CustomerDTO;
import com.api.park_management.models.Customer;
import com.api.park_management.models.Vehicle;
import com.api.park_management.models.payment.RecurringPayment;

@Mapper(componentModel = "spring",
        uses = {VehicleMapper.class, RecurringPaymentMapper.class},
        imports = {RecurringPayment.class, UUID.class, ArrayList.class, Collectors.class, Vehicle.class})
public interface CustomerMapper {
    @Mapping(target = "vehiclesPlates",
            expression = "java(customer.getVehicles().stream()" +
                    ".map(Vehicle::getVehiclePlate)" +
                    ".collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "payments",
            expression = "java(customer.getPayments().stream()" +
                    ".map(RecurringPayment::getIdPayment)" +
                    ".collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "vehicles.associatedCustomer", ignore = true)
    CustomerDTO toDTO(Customer customer);

    @Mapping(target = "vehicles", source = "vehiclesPlates", qualifiedByName = "mapVehiclePlates")
    @Mapping(target = "payments", source = "payments",qualifiedByName = "mapPaymentIds")
    Customer toEntity(CustomerDTO customerDTO);

    @Named("mapPaymentIds")
    default List<RecurringPayment> mapPaymentIds(List<UUID> paymentIds) {
        if (paymentIds == null) return new ArrayList<>();
        return paymentIds.stream()
                .map(id -> {
                    RecurringPayment payment = new RecurringPayment();
                    payment.setIdPayment(id);
                    return payment;
                })
                .collect(Collectors.toList());
    }

    @Named("mapVehiclePlates")
    default List<Vehicle> mapVehiclePlates(List<String> vehiclePlates) {
        if (vehiclePlates == null) return new ArrayList<>();
        return vehiclePlates.stream()
                .map(plate -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehiclePlate(plate);
                    return vehicle;
                })
                .collect(Collectors.toList());
    }

    @Mapping(target = "vehicles", source = "vehiclesPlates", qualifiedByName = "mapVehiclePlates")
    @Mapping(target = "payments", source = "payments", qualifiedByName = "mapPaymentIds")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDTO(CustomerDTO dto,@MappingTarget Customer customer);
}
