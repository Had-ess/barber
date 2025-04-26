package com.youssef.barber.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ManageSlotsActivity extends AppCompatActivity {

    private static final String TAG = "ManageSlotsActivity";


    private LinearLayout slotsContainer;
    private Button buttonSaveSlots;
    private ProgressBar progressBar;


    private DatabaseReference slotsRef;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;


    private List<String> timeSlots;

    private Map<String, Boolean> currentSlotsState = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_slots);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            Toast.makeText(this, "No user logged in. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        slotsRef = FirebaseDatabase.getInstance().getReference()
                .child("barberSlots")
                .child(currentUser.getUid());


        slotsContainer = findViewById(R.id.slotsContainer);
        buttonSaveSlots = findViewById(R.id.buttonSaveSlots);
        progressBar = findViewById(R.id.progressBar);
        timeSlots = generateTimeSlots();
        for (String slot : timeSlots) {
            currentSlotsState.put(slot, false);
        }
        loadBarberSlots();
        buttonSaveSlots.setOnClickListener(v -> saveSlots());
    }


    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 9; hour < 17; hour++) {
            slots.add(String.format("%02d:00 - %02d:30", hour, hour));
            slots.add(String.format("%02d:30 - %02d:00", hour, hour + 1));
        }
        return slots;
    }

    private void loadBarberSlots() {
        progressBar.setVisibility(View.VISIBLE);
        slotsContainer.removeAllViews();

        slotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                        String slotKey = slotSnapshot.getKey();
                        Object slotValue = slotSnapshot.getValue();
                        if (slotKey != null && currentSlotsState.containsKey(slotKey) && slotValue instanceof Boolean) {
                            currentSlotsState.put(slotKey, (Boolean) slotValue);
                        } else if (slotKey != null) {
                            Log.w(TAG, "Mismatch or invalid data found for slot: " + slotKey);
                        }
                    }
                }

                for (final String slot : timeSlots) {
                    CheckBox checkBox = new CheckBox(ManageSlotsActivity.this);
                    checkBox.setText(slot);
                    checkBox.setTextSize(16);
                    checkBox.setPadding(16, 32, 16, 32); // L, T, R, B padding

                    checkBox.setChecked(currentSlotsState.getOrDefault(slot, false));

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        currentSlotsState.put(slot, isChecked); // Update the state map
                        Log.d(TAG, "Slot " + slot + " state changed to: " + isChecked);
                    });

                    slotsContainer.addView(checkBox);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                String errorMessage = databaseError.getMessage() != null ? databaseError.getMessage() : "Unknown database error";
                Toast.makeText(ManageSlotsActivity.this,
                        "Failed to load slots: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load slots: " + errorMessage);
            }
        });
    }

    private void saveSlots() {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> slotsToSave = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : currentSlotsState.entrySet()) {
            if (entry.getValue()) {
                slotsToSave.put(entry.getKey(), true);
            } else {
                slotsToSave.put(entry.getKey(), null);
            }
        }


        slotsRef.updateChildren(slotsToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(ManageSlotsActivity.this,"Availability updated successfully.",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Slots updated successfully.");
                } else {

                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(ManageSlotsActivity.this,"Failed to update slots: " + errorMessage,Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to update slots: " + errorMessage);
                }
            }
        });
    }
}