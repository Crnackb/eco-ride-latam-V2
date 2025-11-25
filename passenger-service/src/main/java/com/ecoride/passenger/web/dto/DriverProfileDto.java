package com.ecoride.passenger.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverProfileDto {
    private Long id;
    private String licenseNo;
    private String carPlate;
    private Integer seatsOffered;
    private String verificationStatus;
}