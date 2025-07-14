package com.example.backend.models;

public class PasswordResetRequest {
    private String email;

    public PasswordResetRequest() {}

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
}
