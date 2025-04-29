package com.youssef.barber.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordActivity extends AppCompatActivity{

    private EditText editTextEmail;
    private Button buttonResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        progressBar = findViewById(R.id.progressBar);

        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Password reset email sent. Please check your inbox.",Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(ForgotPasswordActivity.this,"Failed to send reset email: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
