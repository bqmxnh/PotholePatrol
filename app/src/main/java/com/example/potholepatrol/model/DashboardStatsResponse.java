package com.example.potholepatrol.model;

public class DashboardStatsResponse {
    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int total;
        private int falls;
        private int distance_traveled;

        public int getTotal() {
            return total;
        }

        public int getFalls() {
            return falls;
        }

        public int getDistanceTraveled() {
            return distance_traveled;
        }
    }
}

