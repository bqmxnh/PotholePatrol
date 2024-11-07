package com.example.potholepatrol.model;

public class NewPasswordRequest {
    private String email;
    private String newPassword;
    private String resetToken;

    public NewPasswordRequest(String email, String resetToken, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.resetToken= resetToken;
    }
}
