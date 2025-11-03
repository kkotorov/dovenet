package com.richmax.dovenet.dto;

public class ChangeEmailRequest {
    private String newEmail;
    private String password; // to verify user

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
