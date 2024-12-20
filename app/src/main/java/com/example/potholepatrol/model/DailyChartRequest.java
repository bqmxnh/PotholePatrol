package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class DailyChartRequest {
    private String startDate;
    private String endDate;

    // Constructor
    public DailyChartRequest(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}




