package com.zaidiapps.sixcameraradar;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceJobIntentService extends JobIntentService {

    private static final String TAG = GeofenceJobIntentService.class.getSimpleName();

    public static final String CHANNEL_ID = "100";
    private static final int GEOFENCE_JOB_ID = 100;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceJobIntentService.class, GEOFENCE_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        handleEvent(GeofencingEvent.fromIntent(intent));
    }

    private void handleEvent(GeofencingEvent geofencingEvent) {

        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

            databaseHelper.getCameras(cameras -> {
                for (Geofence geofence : geofences) {
                    Camera camera = databaseHelper.getCameraById(geofence.getRequestId());
                    generateNotification(camera);
                }
            });
        }
    }

    private void generateNotification(Camera camera) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(
                        camera.getType() == Camera.Type.SPEED ? R.string.speed : R.string.red_light))
                .setContentText(camera.getName())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTimeoutAfter(300000)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}
