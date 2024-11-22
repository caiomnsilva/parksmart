package com.api.park_management.models;

import com.api.park_management.models.enums.VehicleType;
import com.api.park_management.models.payment.HourlyPayment;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_VEHICLES")
@Data
public class Vehicle implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idVehicle;

    @Column(nullable = false, unique = true)
    private String vehiclePlate;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    private LocalDateTime entryTime;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer associatedCustomer;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "currentVehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Spot currentSpot;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "associatedVehicle", fetch = FetchType.LAZY)
    private HourlyPayment payment;
}
