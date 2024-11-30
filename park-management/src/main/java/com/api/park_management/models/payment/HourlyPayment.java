package com.api.park_management.models.payment;

import com.api.park_management.models.Vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HourlyPayment extends Payment {

    @PositiveOrZero
    @Column(nullable = false)
    private int hoursParked = 0;

    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime entryTime = LocalDateTime.now();

    @FutureOrPresent
    private LocalDateTime exitTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle payerVehicle;

}