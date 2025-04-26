package com.youssef.barber.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = "StatusActivity";

    private TextView textViewCurrentAppointment;
    private RadioGroup radioGroupStatus, radioGroupAppointment;
    private RadioButton radioButtonOpen, radioButtonClosed;
    private RadioButton radioButtonInProgress, radioButtonCompleted;
    private Button buttonUpdateStatus;
    private ProgressBar progressBar;

    private DatabaseReference statusRef;
    private DatabaseReference appointmentsRef;
    private FirebaseUser currentUser;
    private String currentAppointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {

            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        statusRef = FirebaseDatabase.getInstance().getReference().child("barberStatus").child(currentUser.getUid());

        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        textViewCurrentAppointment = findViewById(R.id.textViewCurrentAppointment);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        radioGroupAppointment = findViewById(R.id.radioGroupAppointment);
        radioButtonOpen = findViewById(R.id.radioButtonOpen);
        radioButtonClosed = findViewById(R.id.radioButtonClosed);
        radioButtonInProgress = findViewById(R.id.radioButtonInProgress);
        radioButtonCompleted = findViewById(R.id.radioButtonCompleted);
        buttonUpdateStatus = findViewById(R.id.buttonUpdateStatus);
        progressBar = findViewById(R.id.progressBar);

        loadBarberStatus();

        loadCurrentAppointment();

        buttonUpdateStatus.setOnClickListener(v -> updateStatus());
    }

    private void loadBarberStatus() {
        progressBar.setVisibility(View.VISIBLE);
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if (status != null) {
                        if (status.equals("open")) {
                            radioButtonOpen.setChecked(true);
                        } else if (status.equals("closed")){
                            radioButtonClosed.setChecked(true);
                        } else {
                            Log.e(TAG, "loadBarberStatus: Unknown status: " + status);
                            radioButtonClosed.setChecked(true); //Or set a default
                        }
                    } else {
                        Log.e(TAG, "loadBarberStatus: Status is null");
                        radioButtonOpen.setChecked(true); // Or set a default
                    }
                } else {
                    Log.d(TAG, "loadBarberStatus: Status does not exist, setting default");
                    radioButtonOpen.setChecked(true);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "loadBarberStatus: onCancelled", databaseError.toException());
                Toast.makeText(StatusActivity.this,"Failed to load status: " + databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentAppointment() {
        progressBar.setVisibility(View.VISIBLE);
        appointmentsRef.orderByChild("barberId").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                boolean foundInProgress = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String status = snapshot.child("status").getValue(String.class);
                    if (status != null && status.equals("in-progress")) {
                        foundInProgress = true;
                        currentAppointmentId = snapshot.getKey();
                        String customerName = snapshot.child("customerName").getValue(String.class);
                        textViewCurrentAppointment.setText("Current Appointment: " + customerName);
                        radioButtonInProgress.setChecked(true);
                        break;
                    }
                }
                if (!foundInProgress){
                    textViewCurrentAppointment.setText("No current appointment");
                    radioButtonCompleted.setChecked(true);
                    currentAppointmentId = null;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "loadCurrentAppointment: onCancelled", databaseError.toException());
                Toast.makeText(StatusActivity.this,"Failed to load appointments: " + databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus() {
        progressBar.setVisibility(View.VISIBLE);
        String status = radioButtonOpen.isChecked() ? "open" : "closed";
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        statusRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Barber status updated successfully");
                Toast.makeText(StatusActivity.this, "Barber status updated", Toast.LENGTH_SHORT).show();

                if (currentAppointmentId != null) {
                    String appointmentStatus = radioButtonCompleted.isChecked() ? "completed" : "in-progress";
                    updates.clear();
                    updates.put("status", appointmentStatus);
                    appointmentsRef.child(currentAppointmentId).updateChildren(updates)
                            .addOnCompleteListener(appointmentTask -> {
                                progressBar.setVisibility(View.GONE);
                                if (appointmentTask.isSuccessful()) {
                                    Log.d(TAG, "Appointment status updated successfully");
                                    Toast.makeText(StatusActivity.this,"Status updated successfully",Toast.LENGTH_SHORT).show();
                                    if (appointmentStatus.equals("completed")) {
                                        currentAppointmentId = null;
                                        textViewCurrentAppointment.setText("No current appointment");
                                    }
                                } else {
                                    Log.e(TAG, "Failed to update appointment status", appointmentTask.getException());
                                    Toast.makeText(StatusActivity.this,"Failed to update appointment: " + appointmentTask.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                }

            } else {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to update barber status", task.getException());
                Toast.makeText(StatusActivity.this,"Failed to update status: " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
