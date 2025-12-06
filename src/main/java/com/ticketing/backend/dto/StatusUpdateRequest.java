package com.ticketing.backend.dto;

import com.ticketing.backend.model.TicketStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private TicketStatus status;
}
