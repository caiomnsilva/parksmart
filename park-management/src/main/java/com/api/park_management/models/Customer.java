package com.api.park_management.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.api.park_management.enums.CustomerType;
import com.api.park_management.enums.converter.CustomerTypeConverter;
import com.api.park_management.models.payment.RecurringPayment;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="TB_CUSTOMERS")
@Data
public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idCustomer;

    @CPF
    @Column(nullable = false, unique = true)
    private String cpf;

    @Size(min = 5, max = 100)
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Positive
    @Length(min=11, max=11)
    @Column(nullable = false, unique = true)
    private String phone;

    @Convert(converter = CustomerTypeConverter.class)
    @Column(nullable = false)
    private CustomerType type;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "associatedCustomer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Vehicle> vehicles = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "payerCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecurringPayment> payments = new ArrayList<>();

}
