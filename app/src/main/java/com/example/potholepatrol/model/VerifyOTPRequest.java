package com.example.potholepatrol.model;

public class VerifyOTPRequest {
    private String email;
    private String otp;

    public VerifyOTPRequest(String email, String otp)
    {
        this.email=email;
        this.otp=otp;
    }
}
