package com.example.potholepatrol.model;

public class PotholeRequest {
    private Location location;
    private Description description;
    private Severity severity;

    public PotholeRequest(Location location, Description description, Severity severity) {
        this.location = location;
        this.description = description;
        this.severity = severity;
    }

    public static class Location {
        private String address;
        private Coordinates coordinates;

        public Location(String address, double latitude, double longitude) {
            this.address = address;
            this.coordinates = new Coordinates(latitude, longitude);
        }

        public static class Coordinates {
            private double latitude;
            private double longitude;

            public Coordinates(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }

    public static class Description {
        private String dimension;
        private String depth;
        private String shape;

        public Description(String dimension, String depth, String shape) {
            this.dimension = dimension;
            this.depth = depth;
            this.shape = shape;
        }
    }

    public static class Severity {
        private String level;
        private boolean causesDamage;

        public Severity(String level, boolean causesDamage) {
            this.level = level;
            this.causesDamage = causesDamage;
        }
    }
}
