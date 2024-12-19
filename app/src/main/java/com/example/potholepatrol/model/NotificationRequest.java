package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class NotificationRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private NotificationData data;

    public NotificationRequest(String title, String message, String type, NotificationData data) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.data = data;
    }

    public static class NotificationData {
        @SerializedName("coordinates")
        private Coordinates coordinates;

        public NotificationData(Coordinates coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class Coordinates {
        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}