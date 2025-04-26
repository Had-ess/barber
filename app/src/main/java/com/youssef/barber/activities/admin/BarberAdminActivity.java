package com.youssef.barber.activities.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.youssef.barber.R;
import com.youssef.barber.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarberAdminActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private Button buttonCreateBarber, buttonUpdateBarber;
    private ListView listViewBarbers;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private List<User> barberList;
    private ArrayAdapter<String> adapter;
    private List<String> barberNames;
    private String selectedBarberId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_admin);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonCreateBarber = findViewById(R.id.buttonCreateBarber);
        buttonUpdateBarber = findViewById(R.id.buttonUpdateBarber);
        listViewBarbers = findViewById(R.id.listViewBarbers);
        progressBar = findViewById(R.id.progressBar);

        barberList = new ArrayList<>();
        barberNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barberNames);
        listViewBarbers.setAdapter(adapter);

        loadBarbers();

        buttonCreateBarber.setOnClickListener(v -> createBarberAccount());
        buttonUpdateBarber.setOnClickListener(v -> updateBarberProfile());

        listViewBarbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < barberList.size()) {
                    User selectedBarber = barberList.get(position);
                    if (selectedBarber != null) {
                        selectedBarberId = selectedBarber.getUserId();
                        loadBarberDetails(selectedBarberId);
                    } else {
                        selectedBarberId = null;
                        clearFields();
                        Toast.makeText(BarberAdminActivity.this, "Error: Selected barber data is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectedBarberId = null;
                    clearFields();
                    Toast.makeText(BarberAdminActivity.this, "Error: Invalid selection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadBarbers() {
        progressBar.setVisibility(View.VISIBLE);

        usersRef.orderByChild("userType").equalTo("barber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barberList.clear();
                barberNames.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        barberList.add(user);
                        barberNames.add(user.getName() != null ? user.getName() : "Unnamed Barber");
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = databaseError.getMessage() != null ? databaseError.getMessage() : "Unknown error";
                Toast.makeText(BarberAdminActivity.this, "Failed to load barbers: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBarberDetails(String barberId) {
        if (barberId == null) {
            Toast.makeText(this, "Invalid barber ID.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        usersRef.child(barberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                User barber = snapshot.getValue(User.class);
                if (barber != null) {
                    editTextName.setText(barber.getName() != null ? barber.getName() : "");
                    editTextEmail.setText(barber.getEmail() != null ? barber.getEmail() : "");
                    editTextPhone.setText(barber.getPhone() != null ? barber.getPhone() : "");
                    editTextPassword.setText("");
                    editTextPassword.setHint("Enter new password to change (optional)");
                } else {
                    Toast.makeText(BarberAdminActivity.this, "Barber details not found.", Toast.LENGTH_SHORT).show();
                    clearFields();
                    selectedBarberId = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
                Toast.makeText(BarberAdminActivity.this, "Failed to load barber details: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createBarberAccount() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required for new account");
            editTextPassword.requestFocus();
            return;
        }

        if (validateInputs(name, email, password, phone)) {
            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User barber = new User(firebaseUser.getUid(), name, email, phone, "barber");

                            usersRef.child(firebaseUser.getUid()).setValue(barber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> dbTask) {
                                    progressBar.setVisibility(View.GONE);
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(BarberAdminActivity.this, "Barber account created successfully.", Toast.LENGTH_SHORT).show();
                                        clearFields();

                                    } else {
                                        String dbError = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown database error";
                                        Toast.makeText(BarberAdminActivity.this, "Account created, but failed to save barber data: " + dbError, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(BarberAdminActivity.this, "Account created, but failed to get user details.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String authError = task.getException() != null ? task.getException().getMessage() : "Unknown authentication error";
                        Toast.makeText(BarberAdminActivity.this, "Failed to create account: " + authError, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void updateBarberProfile() {
        if (selectedBarberId == null) {
            Toast.makeText(this, "Please select a barber from the list first.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();

        if (!validateInputs(name, email, "dummyPasswordIfNotEmpty", phone)) {
            if (editTextPassword.getError() != null && !editTextPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Password must be at least 6 characters if provided.", Toast.LENGTH_SHORT).show();
            } else if (editTextPassword.getError() == null) {
                return;
            }
        }


        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);


        usersRef.child(selectedBarberId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(BarberAdminActivity.this, "Barber profile updated successfully (Database only).", Toast.LENGTH_SHORT).show();
                    clearFields();
                    selectedBarberId = null;
                    editTextPassword.setHint("Password");
                } else {
                    String dbError = task.getException() != null ? task.getException().getMessage() : "Unknown database error";
                    Toast.makeText(BarberAdminActivity.this, "Failed to update barber profile: " + dbError, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean validateInputs(String name, String email, String password, String phone) {
        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextPhone.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Valid email is required");
            if (isValid) editTextEmail.requestFocus();
            isValid = false;
        }

        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters if provided");
            if (isValid) editTextPassword.requestFocus();
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Phone number is required");
            if (isValid) editTextPhone.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextPhone.setText("");
        editTextPassword.setHint("Password");
        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextPhone.setError(null);

        listViewBarbers.clearChoices();
        adapter.notifyDataSetChanged();
        selectedBarberId = null;
    }
}
