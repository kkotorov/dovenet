package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.dto.ContactSupportRequest;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.security.JwtUtil;
import com.richmax.dovenet.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public EmailServiceImpl(JavaMailSender mailSender, JwtUtil jwtUtil, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("verify@dovenet.eu");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


    @Override
    public void sendVerificationEmail(User user) {
        String verifyLink = "https://www.dovenet.eu/verify-email?token=" + user.getVerificationToken();
        String subject = "Confirm your Dovenet account";
        String body = "Hello " + user.getUsername() + ",\n\n"
                + "Welcome to Dovenet! Please confirm your email by clicking the link below:\n\n"
                + verifyLink + "\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "Best regards,\n"
                + "The Dovenet Team";

        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendSupportEmail(String fromEmail, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        // We send TO the support email
        message.setTo("dovenet.info@gmail.com");
        
        // We set the subject
        message.setSubject("Support Request: " + subject);

        message.setFrom("verify@dovenet.eu"); 
        message.setReplyTo(fromEmail);
        
        String fullBody = "Message from user: " + fromEmail + "\n\n" + messageBody;
        message.setText(fullBody);
        
        mailSender.send(message);
    }

    @Override
    public void handleSupportRequest(String authHeader, ContactSupportRequest request) {
        String userEmail = null;

        // 1. Try to get email from JWT if available
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.replace("Bearer ", "");
                // Use the new method and find by email
                String emailFromToken = jwtUtil.extractEmail(token);
                Optional<User> user = userRepository.findByEmail(emailFromToken);
                if (user.isPresent()) {
                    userEmail = user.get().getEmail();
                }
            } catch (Exception e) {
                // Token invalid or expired, ignore and fall back to request body
            }
        }

        // 2. If not found in JWT, check request body
        if (userEmail == null) {
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                userEmail = request.getEmail();
            } else {
                throw new IllegalArgumentException("Email is required (either via login or request body)");
            }
        }

        if (request.getSubject() == null || request.getMessage() == null) {
            throw new IllegalArgumentException("Subject and message are required");
        }

        sendSupportEmail(userEmail, request.getSubject(), request.getMessage());
    }

    @Override
    public void sendEmailChangeConfirmation(String newEmail, String token) {
        String confirmLink = "https://www.dovenet.eu/confirm-email-change?token=" + token;
        String subject = "Confirm Email Change";
        String body = "Hello,\n\n"
                + "We received a request to change your Dovenet account email to this address.\n"
                + "Please confirm this change by clicking the link below:\n\n"
                + confirmLink + "\n\n"
                + "This link will expire in 1 hour.\n\n"
                + "Best regards,\n"
                + "The Dovenet Team";

        sendEmail(newEmail, subject, body);
    }

    @Override
    public void sendEmailChangeNotification(String oldEmail) {
        String subject = "Security Alert: Email Changed";
        String body = "Hello,\n\n"
                + "The email address associated with your Dovenet account has been changed.\n"
                + "If you did not authorize this change, please contact support immediately. https://www.dovenet.eu/contact\n\n"
                + "Best regards,\n"
                + "The Dovenet Team";

        sendEmail(oldEmail, subject, body);
    }

    @Override
    public void sendPasswordChangedNotification(String email) {
        String subject = "Security Alert: Password Changed";
        String body = "Hello,\n\n"
                + "The password for your Dovenet account has just been changed.\n"
                + "If you did not make this change, please contact support immediately. - https://www.dovenet.eu/contact\n\n"
                + "Best regards,\n"
                + "The Dovenet Team";

        sendEmail(email, subject, body);
    }
}
