package com.ticketing.backend.dto;

import com.ticketing.backend.model.TicketPriority;
import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
}