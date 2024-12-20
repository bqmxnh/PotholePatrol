package com.example.potholepatrol.model;

public class ReportRequest {
    private String description;

    public ReportRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}