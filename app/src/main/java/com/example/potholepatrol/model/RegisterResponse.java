package com.example.potholepatrol.model;

public class RegisterResponse {
    private String accessToken;
    private String refreshToken;
    private String message;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getMessage() {
        return message;
    }
}
