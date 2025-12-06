package com.ticketing.backend.controller;

import com.ticketing.backend.dto.*;
import com.ticketing.backend.model.Comment;
import com.ticketing.backend.model.Rating;
import com.ticketing.backend.model.Ticket;
import com.ticketing.backend.model.TicketPriority;
import com.ticketing.backend.model.TicketStatus;
import com.ticketing.backend.service.TicketService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@CrossOrigin
public class TicketController {

    private final TicketService ticketService;

    // CREATE TICKET (USER)
    @PostMapping
    public Ticket createTicket(@RequestBody TicketRequest request, Principal principal) {
        return ticketService.createTicket(
                principal.getName(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority());
    }

    // GET MY TICKETS (USER)
    @GetMapping("/my")
    public List<Ticket> myTickets(Principal principal) {
        return ticketService.getMyTickets(principal.getName());
    }

    // GET ASSIGNED TICKETS (AGENT)
    @GetMapping("/assigned")
    public List<Ticket> assignedTickets(Principal principal) {
        return ticketService.getAssignedTickets(principal.getName());
    }

    // GET ALL (ADMIN)
    @GetMapping("/all")
    public List<Ticket> allTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{ticketId}")
    public Ticket getTicketById(@PathVariable Long ticketId, Principal principal) {
        return ticketService.getTicketById(ticketId, principal.getName());
    }

    // ASSIGN TICKET (ADMIN)
    @PostMapping("/{ticketId}/assign")
    public Ticket assign(@PathVariable Long ticketId, @RequestBody AssignRequest req) {
        return ticketService.assignTicket(ticketId, req.getAgentId());
    }

    @PostMapping("/{ticketId}/reassign")
    public Ticket reassignTicket(@PathVariable Long ticketId, @RequestBody AssignRequest req) {
        return ticketService.assignTicket(ticketId, req.getAgentId());
    }

    // UPDATE STATUS (SUPPORT_AGENT)
    @PutMapping("/{ticketId}/status")
    public Ticket updateStatus(@PathVariable Long ticketId, @RequestBody StatusUpdateRequest req) {
        return ticketService.updateStatus(ticketId, req.getStatus());
    }

    @PostMapping("/{ticketId}/claim")
    public Ticket claimTicket(@PathVariable Long ticketId, Principal principal) {
        return ticketService.claimTicket(ticketId, principal.getName());
    }

    // ADD COMMENT
    @PostMapping("/{ticketId}/comment")
    public Comment addComment(@PathVariable Long ticketId, @RequestBody CommentRequest req, Principal principal) {
        return ticketService.addComment(ticketId, principal.getName(), req.getText());
    }

    @GetMapping("/{ticketId}/comments")
    public List<Comment> getComments(@PathVariable Long ticketId) {
        return ticketService.getComments(ticketId);
    }

    // CLOSE TICKET (ADMIN)
    @PutMapping("/{ticketId}/close")
    public Ticket close(@PathVariable Long ticketId) {
        return ticketService.closeTicket(ticketId);
    }

    @GetMapping("/unassigned")
    public List<Ticket> unassigned() {
        return ticketService.getUnassignedTickets();
    }

    @GetMapping("/search")
    public List<Ticket> searchTickets(@RequestParam String keyword) {
        return ticketService.search(keyword);
    }

    @GetMapping("/filter/status")
    public List<Ticket> filterByStatus(@RequestParam TicketStatus status) {
        return ticketService.filterByStatus(status);
    }

    @GetMapping("/filter/priority")
    public List<Ticket> filterByPriority(@RequestParam TicketPriority priority) {
        return ticketService.filterByPriority(priority);
    }

    @GetMapping("/filter/agent")
    public List<Ticket> filterByAgent(@RequestParam Long agentId) {
        return ticketService.filterByAgent(agentId);
    }

    @PostMapping("/{ticketId}/upload")
    public String uploadFile(@PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ticketService.uploadFile(ticketId, file);
    }

    @PutMapping("/{ticketId}/priority")
    public Ticket updatePriority(@PathVariable Long ticketId, @RequestBody PriorityUpdateRequest req) {
        return ticketService.updatePriority(ticketId, req.getPriority());
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) throws IOException {
        return ticketService.downloadFile(attachmentId);
    }

    @PostMapping("/{ticketId}/rate")
    public Rating rateTicket(@PathVariable Long ticketId, @RequestBody RatingRequest req) {
        return ticketService.rateTicket(ticketId, req.getStars(), req.getFeedback());
    }

}
