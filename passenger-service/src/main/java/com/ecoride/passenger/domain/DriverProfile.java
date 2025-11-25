package com.ecoride.passenger.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver_profile")
@Data
@NoArgsConstructor
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Column(name = "license_no", nullable = false)
    private String licenseNo;

    @Column(name = "car_plate", nullable = false)
    private String carPlate;

    @Column(name = "seats_offered", nullable = false)
    private Integer seatsOffered;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus; // PENDING, VERIFIED, etc.
}