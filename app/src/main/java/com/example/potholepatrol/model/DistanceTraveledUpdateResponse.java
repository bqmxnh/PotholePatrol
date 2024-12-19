package com.example.potholepatrol.model;

public class DistanceTraveledUpdateResponse {
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
        private double distance_traveled;

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
            return distance_traveled;
        }

        public void setDistanceTraveled(double distanceTraveled) {
            this.distance_traveled = distanceTraveled;
        }
    }
}

