package com.ticketing.backend.dto;

import com.ticketing.backend.model.TicketPriority;
import lombok.Data;

@Data
public class PriorityUpdateRequest {
    private TicketPriority priority;
}
