package com.zaidiapps.sixcameraradar;

import static com.zaidiapps.sixcameraradar.Utils.checkPermissions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static  final String TAG = MainActivity.class.getSimpleName();

    private final ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            Log.i(TAG, "PERMISSION GRANTED");
                            setupGeofences();
                        } else {
                            Log.w(TAG, "PERMISSION DENIED");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        if (!checkPermissions(this)) {
            requestPermissions();
        } else {
            setupGeofences();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "Radar camera alerts";
        String description = "Speed and red light camera alerts";

        NotificationChannel channel = new NotificationChannel(
                GeofenceJobIntentService.CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setBypassDnd(true);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= 29 &&
                checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
            requestPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void setupGeofences() {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                GeofenceHelper geofenceHelper = new GeofenceHelper(this);
                GeofencingClient geofencingClient =
                        LocationServices.getGeofencingClient(this);

                geofenceHelper.setupGeofences(
                        location.getLatitude(),
                        location.getLongitude(),
                        geofencingClient
                );
            } else {
                Log.e(TAG, "Location null");
            }
        });
    }
}