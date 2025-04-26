package com.youssef.barber.activities.customer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.youssef.barber.R;
import com.youssef.barber.adapters.AppointmentAdapter;
import com.youssef.barber.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);

        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAppointments.setAdapter(appointmentAdapter);

        loadUserAppointments();
    }

    private void loadUserAppointments() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        appointmentList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                appointment.setId(snapshot.getKey());
                                appointmentList.add(appointment);
                            }
                        }
                        appointmentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(AppointmentsActivity.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}