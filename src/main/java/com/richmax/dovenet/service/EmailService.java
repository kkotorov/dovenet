package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.User;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    void sendVerificationEmail(User user);
}
