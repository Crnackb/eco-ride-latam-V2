package com.ecoride.passenger.web.dto;

import lombok.Data;

@Data
public class CreateDriverProfileRequest {
    private String licenseNo;
    private String carPlate;
    private Integer seatsOffered;
}