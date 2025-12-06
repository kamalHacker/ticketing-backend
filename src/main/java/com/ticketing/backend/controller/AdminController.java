package com.ticketing.backend.controller;

import com.ticketing.backend.model.Ticket;
import com.ticketing.backend.model.User;
import com.ticketing.backend.repository.UserRepository;
import com.ticketing.backend.repository.TicketRepository;
import com.ticketing.backend.service.TicketService;
import lombok.RequiredArgsConstructor;

import com.ticketing.backend.dto.RoleUpdateRequest;
import com.ticketing.backend.model.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    // Admin: Get ALL tickets
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/agents")
    public List<User> getAgents() {
        return userRepository.findByRole(Role.SUPPORT_AGENT);
    }

    @PostMapping("/users/{id}/role")
    public User updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(req.getRole());
        return userRepository.save(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        boolean created = ticketRepository.existsByCreatedById(id);
        boolean assigned = ticketRepository.existsByAssignedToId(id);

        if (created || assigned) {
            return ResponseEntity.status(400)
                    .body("Cannot delete user — they still have assigned or created tickets.");
        }

        System.out.println("Authorities = " +
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        userRepository.deleteById(id);

        return ResponseEntity.ok("User deleted");
    }

    // Admin: Get all users (USER + AGENT + ADMIN)
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
