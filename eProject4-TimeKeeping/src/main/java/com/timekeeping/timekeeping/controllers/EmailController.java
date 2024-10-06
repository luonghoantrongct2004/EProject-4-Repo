package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Value("${sendgrid.from.email}")  // You can configure this in application.properties
    private String fromEmail;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String messageContent) {

        // Validate parameters
        if (to == null || subject == null || messageContent == null) {
            return ResponseEntity.badRequest().body("Required fields are missing.");
        }

        // Send email using SendGrid
        try {
            emailService.sendEmailWithSendGrid(to, subject, messageContent, fromEmail);
            return ResponseEntity.ok("Notification sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send notification: " + e.getMessage());
        }
    }
}
