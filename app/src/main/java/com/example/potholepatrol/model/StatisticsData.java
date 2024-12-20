package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class StatisticsData {

    @SerializedName("date")
    private String date;

    @SerializedName("total")
    private int total;

    @SerializedName("Low")
    private int low;

    @SerializedName("Medium")
    private int medium;

    @SerializedName("High")
    private int high;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }
}
