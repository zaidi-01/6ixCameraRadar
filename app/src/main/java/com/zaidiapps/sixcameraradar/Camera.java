package com.zaidiapps.sixcameraradar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Camera {

    public enum Type {
        SPEED,
        RED_LIGHT
    }

    private String id, name;
    private Type type;
    private double latitude, longitude;

    public Camera() {}

    public Camera(String id, String name, String type, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.type = Type.valueOf(type);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
