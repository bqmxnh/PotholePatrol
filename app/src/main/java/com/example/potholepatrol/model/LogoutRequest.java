package com.example.potholepatrol.model;

public class LogoutRequest {
    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

