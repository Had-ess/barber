package com.youssef.barber;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.activities.admin.BarberAdminActivity;
import com.youssef.barber.activities.admin.ManageSlotsActivity;
import com.youssef.barber.activities.admin.StatusActivity;
import com.youssef.barber.activities.auth.LoginActivity;
import com.youssef.barber.activities.auth.ProfileActivity;
import com.youssef.barber.activities.customer.AppointmentsActivity;
import com.youssef.barber.activities.customer.BookingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String userType = "customer"; // Default to customer

    // UI Components
    private Button btnBookAppointment, btnMyAppointments;
    private Button btnManageSlots, btnUpdateStatus;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Initialize UI components
        tvWelcome = findViewById(R.id.tvWelcome);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnManageSlots = findViewById(R.id.btnManageSlots);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        // Load user data and setup UI accordingly
        loadUserData();

        // Set click listeners
        btnBookAppointment.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
        btnMyAppointments.setOnClickListener(v -> startActivity(new Intent(this, AppointmentsActivity.class)));
        btnManageSlots.setOnClickListener(v -> startActivity(new Intent(this, ManageSlotsActivity.class)));
        btnUpdateStatus.setOnClickListener(v -> startActivity(new Intent(this, StatusActivity.class)));
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    userType = dataSnapshot.child("userType").getValue(String.class);

                    tvWelcome.setText("Welcome, " + (name != null ? name : "User"));

                    // Show/hide buttons based on user type
                    if (userType != null && userType.equals("barber")) {
                        btnBookAppointment.setVisibility(View.GONE);
                        btnMyAppointments.setVisibility(View.GONE);
                        btnManageSlots.setVisibility(View.VISIBLE);
                        btnUpdateStatus.setVisibility(View.VISIBLE);
                    } else {
                        btnBookAppointment.setVisibility(View.VISIBLE);
                        btnMyAppointments.setVisibility(View.VISIBLE);
                        btnManageSlots.setVisibility(View.GONE);
                        btnUpdateStatus.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Hide admin menu if not admin
        MenuItem adminItem = menu.findItem(R.id.action_admin);
        adminItem.setVisible(false); // Set to true if user is admin

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_admin) {
            startActivity(new Intent(this, BarberAdminActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to this activity
        if (mAuth.getCurrentUser() != null) {
            loadUserData();
        }
    }
}