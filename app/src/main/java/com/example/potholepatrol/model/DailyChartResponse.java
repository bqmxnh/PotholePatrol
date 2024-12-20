package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DailyChartResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<StatisticsData> data;

    // Getter and setter methods
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StatisticsData> getData() {
        return data;
    }

    public void setData(List<StatisticsData> data) {
        this.data = data;
    }
}
