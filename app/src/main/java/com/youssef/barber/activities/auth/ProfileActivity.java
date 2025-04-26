package com.youssef.barber.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;
import com.youssef.barber.activities.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private TextView textViewEmail, textViewUserType;
    private EditText editTextName, editTextPhone;
    private Button buttonUpdate, buttonChangePassword, buttonLogout;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    private String currentUserType; // Added declaration for currentUserType

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in. Redirecting to login.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewUserType = findViewById(R.id.textViewUserType);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonLogout = findViewById(R.id.buttonLogout);
        progressBar = findViewById(R.id.progressBar);



        loadUserData();

        buttonUpdate.setOnClickListener(v -> updateProfile());
        buttonChangePassword.setOnClickListener(view -> startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class))); // Make sure ChangePasswordActivity exists and is imported
        buttonLogout.setOnClickListener(view -> logoutUser());
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);

        if (userRef == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ProfileActivity.this,"Error: User reference not initialized.",Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {

                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    currentUserType = snapshot.child("userType").getValue(String.class);


                    if (currentUser != null) {
                        textViewEmail.setText(currentUser.getEmail());
                    } else {
                        textViewEmail.setText("Email not available");
                    }


                    textViewUserType.setText("Account Type: " + (currentUserType != null ? currentUserType : "Not set"));
                    editTextName.setText(name != null ? name : "");
                    editTextPhone.setText(phone != null ? phone : "");

                } else {
                    Toast.makeText(ProfileActivity.this, "User profile data not found.", Toast.LENGTH_SHORT).show();

                    if (currentUser != null) {
                        textViewEmail.setText(currentUser.getEmail());
                    }
                    textViewUserType.setText("Account Type: Not set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(ProfileActivity.this, "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            // Corrected: Set error and focus on editTextPhone
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        if (userRef == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ProfileActivity.this,"Error: Cannot update profile. User reference not initialized.",Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child("name").setValue(name);
        userRef.child("phone").setValue(phone).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(ProfileActivity.this, "Failed to update profile: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}