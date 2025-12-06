package com.ticketing.backend.service;

import com.ticketing.backend.model.*;
import com.ticketing.backend.repository.AttachmentRepository;
import com.ticketing.backend.repository.CommentRepository;
import com.ticketing.backend.repository.TicketRepository;
import com.ticketing.backend.repository.UserRepository;
import com.ticketing.backend.repository.RatingRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

        private final TicketRepository ticketRepository;
        private final UserRepository userRepository;
        private final CommentRepository commentRepository;
        private final EmailService emailService;
        private final AttachmentRepository attachmentRepository;
        private final RatingRepository ratingRepository;

        // CREATE TICKET (USER)
        public Ticket createTicket(String email, String title, String description, TicketPriority priority) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // User agent = userRepository.findFirstByRole(Role.SUPPORT_AGENT)
                // .orElseThrow(() -> new RuntimeException("No support agent available"));

                Ticket ticket = Ticket.builder()
                                .title(title)
                                .description(description)
                                .status(TicketStatus.OPEN)
                                .createdBy(user)
                                // .assignedTo(agent)
                                .priority(priority != null ? priority : TicketPriority.MEDIUM)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Ticket savedTicket = ticketRepository.save(ticket);

                // Send email notification to user
                emailService.sendEmail(
                                user.getEmail(),
                                "Ticket Created: " + savedTicket.getTitle(),
                                "Your ticket has been created with ID: " + savedTicket.getId());

                // Send email notification to assigned agent
                // emailService.sendEmail(
                // agent.getEmail(),
                // "New Ticket Assigned: " + savedTicket.getTitle(),
                // "A new ticket has been assigned to you with ID: " + savedTicket.getId());

                return savedTicket;
        }

        // GET MY TICKETS (USER)
        public List<Ticket> getMyTickets(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return ticketRepository.findByCreatedBy(user);
        }

        // GET ASSIGNED TICKETS (SUPPORT AGENT)
        public List<Ticket> getAssignedTickets(String email) {
                User agent = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Support agent not found"));

                return ticketRepository.findByAssignedTo(agent);
        }

        // GET ALL TICKETS (ADMIN)
        public List<Ticket> getAllTickets() {
                return ticketRepository.findAll();
        }

        // ASSIGN TICKET (ADMIN)
        public Ticket assignTicket(Long ticketId, Long agentId) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                User agent = userRepository.findById(agentId)
                                .orElseThrow(() -> new RuntimeException("Agent not found"));

                ticket.setAssignedTo(agent);
                ticket.setUpdatedAt(LocalDateTime.now());

                emailService.sendEmail(
                                agent.getEmail(),
                                "New Ticket Assigned" + ticket.getTitle(),
                                "Ticket #" + ticketId + " has been assigned to you.");

                return ticketRepository.save(ticket);
        }

        public List<Ticket> getUnassignedTickets() {
                return ticketRepository.findByAssignedToIsNull();
        }

        // UPDATE STATUS (SUPPORT_AGENT)
        public Ticket updateStatus(Long ticketId, TicketStatus status) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                ticket.setStatus(status);
                ticket.setUpdatedAt(LocalDateTime.now());

                emailService.sendEmail(
                                ticket.getCreatedBy().getEmail(),
                                "Ticket Status Updated",
                                "Ticket #" + ticketId + " status changed to: " + status);

                return ticketRepository.save(ticket);
        }

        public Ticket updatePriority(Long ticketId, TicketPriority priority) {
                Ticket t = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                t.setPriority(priority);
                t.setUpdatedAt(LocalDateTime.now());
                return ticketRepository.save(t);
        }

        public Ticket claimTicket(Long ticketId, String agentEmail) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                // If already assigned
                if (ticket.getAssignedTo() != null)
                        throw new RuntimeException("Ticket already assigned");

                User agent = userRepository.findByEmail(agentEmail)
                                .orElseThrow(() -> new RuntimeException("Agent not found"));

                ticket.setAssignedTo(agent); // <-- correct property
                ticket.setStatus(TicketStatus.IN_PROGRESS);
                ticket.setUpdatedAt(LocalDateTime.now());

                return ticketRepository.save(ticket);
        }

        // ADD COMMENT (USER + SUPPORT AGENT)
        public Comment addComment(Long ticketId, String email, String text) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Comment comment = Comment.builder()
                                .text(text)
                                .ticket(ticket)
                                .user(user)
                                .createdAt(LocalDateTime.now())
                                .build();

                return commentRepository.save(comment);
        }

        public List<Comment> getComments(Long ticketId) {
                return commentRepository.findByTicketId(ticketId);
        }

        // CLOSE TICKET (ADMIN)
        public Ticket closeTicket(Long ticketId) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                ticket.setStatus(TicketStatus.CLOSED);
                ticket.setUpdatedAt(LocalDateTime.now());

                emailService.sendEmail(
                                ticket.getCreatedBy().getEmail(),
                                "Ticket Closed",
                                "Ticket #" + ticketId + " has been closed.");

                return ticketRepository.save(ticket);
        }

        public Ticket getTicketById(Long id, String email) {
                Ticket ticket = ticketRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // USER can only see their own tickets
                if (user.getRole() == Role.USER && !ticket.getCreatedBy().getEmail().equals(email)) {
                        throw new RuntimeException("Unauthorized");
                }

                // AGENT can see only assigned tickets
                if (user.getRole() == Role.SUPPORT_AGENT &&
                                (ticket.getAssignedTo() == null || !ticket.getAssignedTo().getEmail().equals(email))) {
                        throw new RuntimeException("Unauthorized");
                }

                // ADMIN can see everything
                return ticket;
        }

        public String uploadFile(Long ticketId, MultipartFile file) throws IOException {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                String folder = "uploads/";
                Files.createDirectories(Paths.get(folder));

                String filePath = folder + UUID.randomUUID() + "_" + file.getOriginalFilename();
                Files.write(Paths.get(filePath), file.getBytes());

                Attachment att = Attachment.builder()
                                .fileName(file.getOriginalFilename())
                                .filePath(filePath)
                                .ticket(ticket)
                                .build();

                attachmentRepository.save(att);

                return "File uploaded successfully";
        }

        public ResponseEntity<Resource> downloadFile(Long id) throws IOException {
                Attachment att = attachmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("File not found"));

                Path path = Paths.get(att.getFilePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + att.getFileName())
                                .body(resource);
        }

        // SEARCH & FILTER TICKETS
        public List<Ticket> search(String keyword) {
                return ticketRepository.findByTitleContainingIgnoreCase(keyword);
        }

        public List<Ticket> filterByStatus(TicketStatus status) {
                return ticketRepository.findByStatus(status);
        }

        public List<Ticket> filterByPriority(TicketPriority priority) {
                return ticketRepository.findByPriority(priority);
        }

        public List<Ticket> filterByAgent(Long agentId) {
                return ticketRepository.findByAssignedToId(agentId);
        }

        public Rating rateTicket(Long ticketId, int stars, String feedback) {
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                Rating rating = Rating.builder()
                                .stars(stars)
                                .feedback(feedback)
                                .ticket(ticket)
                                .build();

                return ratingRepository.save(rating);
        }

}
