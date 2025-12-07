package com.ticketing.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String message) {
        try {
            
            // DEBUG → Confirm Railway is using correct SMTP settings
            System.out.println("SMTP Host = " + System.getenv("SPRING_MAIL_HOST"));
            System.out.println("SMTP Username = " + System.getenv("SPRING_MAIL_USERNAME"));
            System.out.println("Sending email to: " + to);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);

            mailSender.send(mail);
            System.out.println("✅ Email sent!");
        } catch (Exception e) {
            System.out.println("❌ Email send failed: " + e.getMessage());
        }
    }
}
