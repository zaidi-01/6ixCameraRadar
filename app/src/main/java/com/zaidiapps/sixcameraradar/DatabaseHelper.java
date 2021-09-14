package com.zaidiapps.sixcameraradar;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private final DatabaseReference mDatabaseReference;
    private final List<Camera> cameras = new ArrayList<>();

    public DatabaseHelper() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("cameras");
    }

    public void getCameras(final DataStatus dataStatus) {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cameras.clear();
                for (DataSnapshot cameraNode: snapshot.getChildren()) {
                    Camera camera = cameraNode.getValue(Camera.class);
                    cameras.add(camera);
                }
                dataStatus.DataIsLoaded(cameras);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface DataStatus {
        void DataIsLoaded(List<Camera> cameras);
    }

}
