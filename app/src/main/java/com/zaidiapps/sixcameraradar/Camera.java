package com.zaidiapps.sixcameraradar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Camera {

    public static enum Type {
        SPEED,
        RED_LIGHT
    };

    private String NAME;
    private Type TYPE;
    private double LATITUDE, LONGITUDE;

    public Camera() {}

    public Camera(String name, String type, double latitude, double longitude) {
        NAME = name;
        TYPE = Type.valueOf(type);
        LATITUDE = latitude;
        LONGITUDE = longitude;
    }

    public String getName() {
        return NAME;
    }

    public Type getType() {
        return TYPE;
    }

    public double getLatitude() {
        return LATITUDE;
    }

    public double getLongitude() {
        return LONGITUDE;
    }
}
