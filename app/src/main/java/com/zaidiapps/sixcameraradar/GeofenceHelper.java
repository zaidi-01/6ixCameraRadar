package com.zaidiapps.sixcameraradar;

import static com.zaidiapps.sixcameraradar.Utils.checkPermissions;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeofenceHelper extends ContextWrapper {

    public enum Type {
        PARENT,
        CHILD
    }

    private static final String TAG = GeofenceHelper.class.getSimpleName();

    private final PendingIntent pendingIntent;
    
    public GeofenceHelper(Context base) {
        super(base);

        Intent intent = new Intent(base, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                base, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest buildGeofencingRequest(List<Geofence> geofences) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofences(geofences)
                .build();
    }

    private Geofence buildGeofence(GeofenceModel geofenceModel, Type type) {
        double latitude = geofenceModel.getLatitude();
        double longitude = geofenceModel.getLongitude();
        float radius = geofenceModel.getRadius();

        Geofence.Builder geofenceBuilder = new Geofence.Builder()
                .setRequestId(geofenceModel.getId())
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE);

        if (type == Type.PARENT) {
            geofenceBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
        } else {
            geofenceBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        }

        return geofenceBuilder.build();
    }

    public void setupGeofences(
            double userLatitude,
            double userLongitude,
            GeofencingClient geofencingClient) {

        DatabaseHelper databaseHelper = new DatabaseHelper();
        databaseHelper.getCameras(cameras -> {
            List<GeofenceModel> childGeofenceModels = new ArrayList<>();

            sortCamerasByLocation(userLatitude, userLongitude, cameras);
            cameras.subList(0, 49).stream().map(camera -> new GeofenceModel(
                    camera.getId(),
                    camera.getLatitude(),
                    camera.getLongitude(),
                    200f
            )).forEach(childGeofenceModels::add);

            float[] parentRadius = new float[1];
            GeofenceModel farthestGeofenceModel =
                    childGeofenceModels.get(childGeofenceModels.size() - 1);
            Location.distanceBetween(
                    userLatitude,
                    userLongitude,
                    farthestGeofenceModel.getLatitude(),
                    farthestGeofenceModel.getLongitude(),
                    parentRadius);

            setupParentGeofence(userLatitude, userLongitude, parentRadius[0], geofencingClient);
            setupChildGeofences(childGeofenceModels, geofencingClient);
        });
    }

    @SuppressLint("MissingPermission")
    private void setupParentGeofence(
            double latitude,
            double longitude,
            float radius,
            GeofencingClient geofencingClient) {

        GeofenceModel geofenceModel = new GeofenceModel("p0", latitude, longitude, radius);
        Geofence geofence = buildGeofence(geofenceModel, Type.PARENT);

        if (checkPermissions(this)) {
            geofencingClient
                    .addGeofences(
                            buildGeofencingRequest(Collections.singletonList(geofence)),
                            pendingIntent)
                    .addOnSuccessListener(runnable ->
                            Log.i(TAG, "Parent geofence success"))
                    .addOnFailureListener(runnable ->
                            Log.e(TAG, "Parent geofence failure"));
        }
    }

    @SuppressLint("MissingPermission")
    private void setupChildGeofences(
            List<GeofenceModel> geofenceModels,
            GeofencingClient geofencingClient) {

        List<Geofence> geofences = new ArrayList<>();
        geofenceModels.stream().map(geofenceModel -> buildGeofence(geofenceModel, Type.CHILD))
                .forEach(geofences::add);

        if (checkPermissions(this)) {
            geofencingClient
                    .addGeofences(
                            buildGeofencingRequest(geofences),
                            pendingIntent)
                    .addOnSuccessListener(runnable ->
                            Log.i(TAG, "Children geofence success"))
                    .addOnFailureListener(runnable ->
                            Log.e(TAG, "Children geofence failure"));
        }
    }

    private void sortCamerasByLocation(
            double userLatitude,
            double userLongitude,
            List<Camera> cameras) {

        Collections.sort(cameras, (c1, c2) -> {
            float[] distance1 = new float[1];
            float[] distance2 = new float[1];

            Location.distanceBetween(
                    userLatitude,
                    userLongitude,
                    c1.getLatitude(),
                    c1.getLongitude(),
                    distance1);
            Location.distanceBetween(
                    userLatitude,
                    userLongitude,
                    c2.getLatitude(),
                    c2.getLongitude(),
                    distance2);

            return Float.compare(distance1[0], distance2[0]);
        });
    }
}
