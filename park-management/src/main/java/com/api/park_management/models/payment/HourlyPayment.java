package com.api.park_management.models.payment;

import com.api.park_management.models.Vehicle;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("HOURLY")
@Getter
@Setter
public class HourlyPayment extends Payment {
    private int hoursParked;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = true)
    private Vehicle associatedVehicle;
}
