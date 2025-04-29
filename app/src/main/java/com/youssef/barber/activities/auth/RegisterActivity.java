package com.youssef.barber.activities.auth;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;
import com.youssef.barber.models.User;
import com.youssef.barber.activities.auth.LoginActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private RadioGroup radioGroupUserType;
    private RadioButton radioButtonCustomer, radioButtonBarber;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        radioButtonCustomer = findViewById(R.id.radioButtonCustomer);
        radioButtonBarber = findViewById(R.id.radioButtonBarber);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);

        radioButtonCustomer.setChecked(true);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        int selectedId = radioGroupUserType.getCheckedRadioButtonId();
        final String userType = (selectedId == R.id.radioButtonBarber) ? "barber" : "customer";

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Optional fallback: hide progress bar after 10 seconds in case something goes wrong
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Operation timed out. Please try again.", Toast.LENGTH_LONG).show();
            }
        }, 10000);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Registration failed: Could not get user.", Toast.LENGTH_LONG).show();
                    return;
                }

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        sendEmailVerification(firebaseUser);

                        User user = new User(firebaseUser.getUid(), name, email, phone, userType);

                        mDatabase.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(dbTask -> {
                            progressBar.setVisibility(View.GONE);
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                String dbError = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown database error";
                                Log.e("Firebase", "DB error: ", dbTask.getException());
                                Toast.makeText(RegisterActivity.this, "Failed to save user data: " + dbError, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String profileError = profileTask.getException() != null ? profileTask.getException().getMessage() : "Unknown profile update error";
                        Log.e("Firebase", "Profile error: ", profileTask.getException());
                        Toast.makeText(RegisterActivity.this, "Failed to update profile: " + profileError, Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                progressBar.setVisibility(View.GONE);
                String authError = task.getException() != null ? task.getException().getMessage() : "Unknown auth error";
                Log.e("Firebase", "Auth error: ", task.getException());
                Toast.makeText(RegisterActivity.this, "Registration failed: " + authError, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Method to send verification email
    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE); // hide on failure
                    String verifyError = task.getException() != null ? task.getException().getMessage() : "Unknown email verification error";
                    Log.e("Firebase", "Verification email error: ", task.getException());
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email: " + verifyError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}