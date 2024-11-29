package com.api.park_management.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.api.park_management.enums.VehicleType;
import com.api.park_management.enums.converter.VehicleTypeConverter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;

@Entity
@Data
@Table(name = "TB_VEHICLES")
public class Vehicle implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idVehicle;

    @NotBlank
    @Length(min = 7, max = 7)
    @Pattern(regexp="^[A-Z]{3}\\d[A-Z\\d]\\d{2}")
    @Column(nullable = false, unique = true)
    private String vehiclePlate;

    @NotBlank
    @Length(min = 3, max = 25)
    @Column(nullable = false, length = 25)
    private String model;
    
    @NotBlank
    @Length(min = 3, max = 25)
    @Column(nullable = false, length = 25)
    private String color;

    @Convert(converter = VehicleTypeConverter.class)
    @Column(nullable = false)
    private VehicleType type;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer associatedCustomer;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "currentVehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Spot currentSpot;

}
