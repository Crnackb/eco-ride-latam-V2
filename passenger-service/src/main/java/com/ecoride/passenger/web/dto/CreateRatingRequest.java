package com.ecoride.passenger.web.dto;

import lombok.Data;

@Data
public class CreateRatingRequest {
    private Long tripId;
    private Long toPassengerId;
    private Integer score;
    private String comment;
}
