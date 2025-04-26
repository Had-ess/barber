package com.youssef.barber.activities.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.youssef.barber.R;
import com.youssef.barber.models.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CancelAppointmentActivity extends AppCompatActivity {

    private Appointment appointment;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_appointment);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        appointment = (Appointment) getIntent().getSerializableExtra("appointment");

        Button buttonCancel = findViewById(R.id.buttonCancelAppointment);
        buttonCancel.setOnClickListener(v -> cancelAppointment());
    }

    private void cancelAppointment() {
        // Update appointment status
        databaseReference.child("appointments").child(appointment.getId())
                .child("status").setValue("cancelled")
                .addOnSuccessListener(aVoid -> {
                    // Make the time slot available again
                    databaseReference.child("timeSlots").child(appointment.getTimeSlotId())
                            .child("available").setValue(true);

                    Toast.makeText(this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                });
    }
}