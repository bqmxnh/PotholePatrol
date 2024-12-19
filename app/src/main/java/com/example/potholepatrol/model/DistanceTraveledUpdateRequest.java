package com.example.potholepatrol.model;

public class DistanceTraveledUpdateRequest {
    private double distance_traveled;

    public DistanceTraveledUpdateRequest(double distance_traveled) {
        this.distance_traveled = distance_traveled;
    }

    public double getDistance_traveled() {
        return distance_traveled;
    }

    public void setDistance_traveled(double distance_traveled) {
        this.distance_traveled = distance_traveled;
    }
}

