package com.youssef.barber.activities.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.youssef.barber.R;
import com.youssef.barber.adapters.SlotAdapter;
import com.youssef.barber.interfaces.OnSlotSelectedListener;
import com.youssef.barber.models.Appointment;
import com.youssef.barber.models.TimeSlot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity implements OnSlotSelectedListener {

    private RecyclerView recyclerViewSlots;
    private Button buttonConfirmBooking;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private List<TimeSlot> timeSlotList;
    private SlotAdapter slotAdapter;
    private TimeSlot selectedTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        setupRecyclerView();
        loadAvailableSlots();
        setupButtonListener();
    }

    private void initializeViews() {
        recyclerViewSlots = findViewById(R.id.recyclerViewSlots);
        buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);
        progressBar = findViewById(R.id.progressBarBooking);
        databaseReference = FirebaseDatabase.getInstance().getReference("timeSlots");
        timeSlotList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        slotAdapter = new SlotAdapter(timeSlotList, this);
        recyclerViewSlots.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewSlots.setAdapter(slotAdapter);
    }

    private void loadAvailableSlots() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByChild("available").equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        timeSlotList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TimeSlot slot = snapshot.getValue(TimeSlot.class);
                            if (slot != null) {
                                slot.setId(snapshot.getKey());
                                timeSlotList.add(slot);
                            }
                        }
                        slotAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(BookingActivity.this, "Failed to load slots", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupButtonListener() {
        buttonConfirmBooking.setOnClickListener(v -> {
            if (selectedTimeSlot != null) {
                confirmBooking();
            } else {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSlotSelected(TimeSlot timeSlot) {
        selectedTimeSlot = timeSlot;
        buttonConfirmBooking.setEnabled(true);
    }

    private void confirmBooking() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");
        String appointmentId = appointmentsRef.push().getKey();

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setUserId(userId);
        appointment.setTimeSlotId(selectedTimeSlot.getId());
        appointment.setStatus("confirmed");
        appointment.setTimestamp(System.currentTimeMillis());

        appointmentsRef.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    // Update slot availability
                    databaseReference.child(selectedTimeSlot.getId())
                            .child("available").setValue(false);

                    Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}