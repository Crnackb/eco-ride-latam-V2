package com.ecoride.passenger.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerDto {
    private Long id;
    private String name;
    private String email;
    private Double ratingAvg;
}
