package com.ticketing.backend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend = new Resend(System.getenv("RESEND_API_KEY"));

    @Async
    public void sendEmail(String to, String subject, String message) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .from("Ticketing System <onboarding@resend.dev>")
                    .to(to)
                    .subject(subject)
                    .text(message)
                    .build();

            SendEmailResponse response = resend.emails().send(request);

            System.out.println("📧 Email sent! ID = " + response.getId());
        } catch (ResendException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
