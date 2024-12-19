package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<NotificationItem> data;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NotificationItem> getData() {
        return data;
    }

    public void setData(List<NotificationItem> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}