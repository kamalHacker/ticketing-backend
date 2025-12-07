package com.ticketing.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Async
    public void sendEmail(String to, String subject, String message) {
        System.out.println("📧 Sending email via SendGrid...");

        try {
            // Prepare email
            Email from = new Email("satwanikamal2003@gmail.com");
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", message);

            Mail mail = new Mail(from, subject, toEmail, content);

            // Build request
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("📨 SendGrid Status = " + response.getStatusCode());
            System.out.println("📄 SendGrid Body = " + response.getBody());
            System.out.println("🔍 SendGrid Headers = " + response.getHeaders());

        } catch (Exception e) {
            System.err.println("❌ SendGrid Email Failed: " + e.getMessage());
        }
    }
}