package com.example.potholepatrol.model;

public class VerifyOTPResponse {

    private String resetToken;

    public VerifyOTPResponse(String resetToken) {
         this.resetToken = resetToken;
    }

    public String getResetToken() {
        return resetToken;
    }


}
