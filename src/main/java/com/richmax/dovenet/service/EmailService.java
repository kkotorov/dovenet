package com.richmax.dovenet.service;

import com.richmax.dovenet.dto.ContactSupportRequest;
import com.richmax.dovenet.repository.data.User;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendVerificationEmail(User user);

    void sendSupportEmail(String fromEmail, String subject, String message);

    void handleSupportRequest(String authHeader, ContactSupportRequest request);

    void sendEmailChangeConfirmation(String newEmail, String token);
    
    void sendEmailChangeNotification(String oldEmail);

    void sendPasswordChangedNotification(String email);
}
