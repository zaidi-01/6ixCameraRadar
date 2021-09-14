package com.zaidiapps.sixcameraradar;

public class GeofenceModel {

    private final String id;
    private final double latitude, longitude;
    private final float radius;

    public GeofenceModel(String id, double latitude, double longitude, float radius) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRadius() {
        return radius;
    }

}
