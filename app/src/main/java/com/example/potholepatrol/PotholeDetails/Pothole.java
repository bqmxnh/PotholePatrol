package com.example.potholepatrol.PotholeDetails;

import com.google.gson.annotations.SerializedName;

public class Pothole {
    private String id;
    private Location location;
    private Description description;
    private Severity severity;
    private String image;
    private String reportedBy;
    private String createdAt;

    // Getters
    public String getId() { return id; }
    public Location getLocation() { return location; }
    public Description getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public String getImage() { return image; }
    public String getReportedBy() { return reportedBy; }
    public String getCreatedAt() { return createdAt; }

    public static class Location {
        private String address;
        private Coordinates coordinates;

        public String getAddress() { return address; }
        public Coordinates getCoordinates() { return coordinates; }
    }

    public static class Coordinates {
        @SerializedName("coordinates")
        private double[] coordinatesArray;

        public double getLatitude() {
            // MongoDB stores coordinates as [longitude, latitude]
            return coordinatesArray[1];
        }

        public double getLongitude() {
            return coordinatesArray[0];
        }
    }

    public static class Description {
        private String dimension;
        private String depth;
        private String shape;

        public String getDimension() { return dimension; }
        public String getDepth() { return depth; }
        public String getShape() { return shape; }
    }

    public static class Severity {
        private String level;
        private boolean causesDamage;

        public String getLevel() { return level; }
        public boolean isCausesDamage() { return causesDamage; }
    }
}