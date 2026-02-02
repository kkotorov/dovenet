package com.richmax.dovenet.dto;

import lombok.Data;

@Data
public class ContactSupportRequest {
    private String email; // Optional if logged in, required if logged out
    private String subject;
    private String message;
}
