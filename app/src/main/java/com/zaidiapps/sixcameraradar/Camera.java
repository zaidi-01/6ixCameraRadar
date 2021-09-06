package com.zaidiapps.sixcameraradar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Camera {

    private String NAME, TYPE;
    private double LATITUDE, LONGITUDE;

    public Camera() {}

    public Camera(String name, String type, double latitude, double longitude) {
        NAME = name;
        TYPE = type;
        LATITUDE = latitude;
        LONGITUDE = longitude;
    }

    public String getName() {
        return NAME;
    }

    public String getType() {
        return TYPE;
    }

    public double getLatitude() {
        return LATITUDE;
    }

    public double getLongitude() {
        return LONGITUDE;
    }
}
