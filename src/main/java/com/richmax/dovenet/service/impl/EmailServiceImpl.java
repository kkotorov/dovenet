package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
}
