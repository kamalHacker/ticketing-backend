package com.ticketing.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int stars;
    private String feedback;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
}
