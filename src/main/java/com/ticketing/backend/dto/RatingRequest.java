package com.ticketing.backend.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private int stars;
    private String feedback;
}
