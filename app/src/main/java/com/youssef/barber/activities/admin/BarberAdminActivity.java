package com.youssef.barber.activities.admin;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
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

import androidx.appcompat.app.AppCompatActivity;

public class BarberAdminActivity extends AppCompatActivity{

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;
    private Button buttonCreateBarber, buttonUpdateBarber;
    private ListView listViewBarbers;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private List<User> barberList;
    private ArrayAdapter<String> adapter;
    private List<String> barberNames;
    private String selectedBarberId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        buttonCreateBarber.setOnClickListener(v -> CreateBarberAccount());
        buttonUpdateBarber.setOnClickListener(v -> UpdateBarberProfile());

        listViewBarbers.setOnClickListener((parent, view, position, id) -> {
            selectedBarberId = barberList.get(position).getUserId();
            loadBarberdetaills(selectedBarberId);
        });
    }

    private void loadBarbers(){

        progressBar.setVisibility(View.VISIBLE);

        usersRef.orderByChild("userType")



    }



}
