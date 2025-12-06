package com.ticketing.backend.repository;

import com.ticketing.backend.model.Ticket;
import com.ticketing.backend.model.TicketStatus;
import com.ticketing.backend.model.User;
import com.ticketing.backend.model.TicketPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(User user);

    List<Ticket> findByAssignedTo(User user);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByAssignedToId(Long agentId);

    List<Ticket> findByTitleContainingIgnoreCase(String keyword);

    List<Ticket> findByAssignedToIsNull();

    boolean existsByCreatedById(Long userId);

    boolean existsByAssignedToId(Long userId);

}
