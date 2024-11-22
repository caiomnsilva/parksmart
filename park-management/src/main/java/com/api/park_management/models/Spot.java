package com.api.park_management.models;

import com.api.park_management.models.enums.SpotType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_SPOTS")
@Data
public class Spot implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idSpot;

    @Column(nullable = false, unique = true)
    private int spotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotType type;

    @Column(nullable = false)
    private boolean isOcuppied = false;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle currentVehicle;
}
