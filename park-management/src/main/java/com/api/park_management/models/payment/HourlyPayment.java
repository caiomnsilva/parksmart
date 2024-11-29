package com.api.park_management.models.payment;

import com.api.park_management.models.Vehicle;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("HOURLY")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HourlyPayment extends Payment {
    
    @Positive
    private int hoursParked;

    @PastOrPresent
    private LocalDateTime entryTime = LocalDateTime.now();

    @FutureOrPresent
    private LocalDateTime exitTime;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle payerVehicle;

}