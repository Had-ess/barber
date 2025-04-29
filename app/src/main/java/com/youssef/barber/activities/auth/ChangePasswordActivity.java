package com.youssef.barber.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.youssef.barber.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextNewPassword, editTextConfirmPassword;
    private Button buttonChangePassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        progressBar = findViewById(R.id.progressBar);

        buttonChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            editTextNewPassword.setError("Password is required");
            editTextNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editTextNewPassword.setError("Password must be at least 6 characters");
            editTextNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords don't match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Failed to change password: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
