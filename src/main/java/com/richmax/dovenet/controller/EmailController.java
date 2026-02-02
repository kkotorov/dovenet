package com.richmax.dovenet.controller;

import com.richmax.dovenet.dto.ContactSupportRequest;
import com.richmax.dovenet.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String body = request.get("body");

        if (to == null || subject == null || body == null) {
            return ResponseEntity.badRequest().body("Missing 'to', 'subject', or 'body' fields");
        }

        emailService.sendEmail(to, subject, body);
        return ResponseEntity.ok("Email sent successfully");
    }

    @PostMapping("/contact-support")
    public ResponseEntity<String> contactSupport(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ContactSupportRequest request
    ) {
        try {
            emailService.handleSupportRequest(authHeader, request);
            return ResponseEntity.ok("Support request sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
