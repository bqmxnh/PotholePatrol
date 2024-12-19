package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStatsResponse {
    private String status;
    private Data data;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private int total;
        private int falls;

        @SerializedName("distance_traveled")  // Mapping JSON field 'distance_traveled' to Java field 'distanceTraveled'
        private double distanceTraveled;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getFalls() {
            return falls;
        }

        public void setFalls(int falls) {
            this.falls = falls;
        }

        public double getDistanceTraveled() {
            return distanceTraveled;
        }

        public void setDistanceTraveled(double distanceTraveled) {
            this.distanceTraveled = distanceTraveled;
        }
    }
}
