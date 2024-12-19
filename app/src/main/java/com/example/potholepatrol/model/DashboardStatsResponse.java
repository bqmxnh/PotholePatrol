package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class DashboardStatsResponse {
    private String status;
    private Data data;


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
        private BySeverity bySeverity;

        @SerializedName("distance_traveled")
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

        public BySeverity getBySeverity() {
            return bySeverity;
        }

        public void setBySeverity(BySeverity bySeverity) {
            this.bySeverity = bySeverity;
        }

        public double getDistanceTraveled() {
            return distanceTraveled;
        }

        public void setDistanceTraveled(double distanceTraveled) {
            this.distanceTraveled = distanceTraveled;
        }
    }

    public static class BySeverity {
        private int High;
        private int Medium;
        private int Low;

        public int getHigh() {
            return High;
        }

        public void setHigh(int high) {
            High = high;
        }

        public int getMedium() {
            return Medium;
        }

        public void setMedium(int medium) {
            Medium = medium;
        }

        public int getLow() {
            return Low;
        }

        public void setLow(int low) {
            Low = low;
        }
    }
}

