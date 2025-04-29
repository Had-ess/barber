package com.youssef.barber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.youssef.barber.MainActivity;
import com.youssef.barber.R;
import com.youssef.barber.activities.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Check authentication state after splash duration
            checkAuthState();
        }, SPLASH_DURATION);
    }

    private void checkAuthState() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is logged in, proceed to main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // No user logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // Close splash activity
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove any pending handlers to prevent memory leaks
        new Handler().removeCallbacksAndMessages(null);
    }
}