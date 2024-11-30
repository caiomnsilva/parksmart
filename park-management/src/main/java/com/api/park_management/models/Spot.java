package com.api.park_management.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import com.api.park_management.enums.SpotType;
import com.api.park_management.enums.converter.SpotTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Hidden
@Entity
@Data
@Table(name = "TB_SPOTS")
public class Spot implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idSpot;
    
    @PositiveOrZero
    @NotNull
    @Column(nullable = false, unique = true)
    private int spotNumber;


    @Convert(converter = SpotTypeConverter.class)
    @Column(nullable = false)
    private SpotType type;

    @Column(nullable = false)
    private boolean occupied = false;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    @JsonBackReference
    private Vehicle currentVehicle;

}
