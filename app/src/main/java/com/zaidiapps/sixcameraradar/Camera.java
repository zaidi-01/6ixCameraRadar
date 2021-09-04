package com.zaidiapps.sixcameraradar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Camera {

    private final String name, type;
    private final float latitude, longitude;

    public Camera(String name, String type, float latitude, float longitude) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
