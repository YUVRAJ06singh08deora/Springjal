package com.example.springjal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import android.widget.TextView;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Admins
public class Admin_Home extends AppCompatActivity {
    RelativeLayout recievedActivities, SearchSpringData, analyseSpringBoundaries, Analytics, springPlot, sendMessage;
    private TextView usernameTextView;
    private TextView roleTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        recievedActivities = findViewById(R.id.recievedActivity);
        SearchSpringData = findViewById(R.id.spring_data_filter);
        analyseSpringBoundaries = findViewById(R.id.analysespringshed);
        Analytics = findViewById(R.id.analytics);
        springPlot = findViewById(R.id.spring_plot_all);
        sendMessage = findViewById(R.id.sendmessage);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find TextViews by their IDs
        usernameTextView = findViewById(R.id.username);
        roleTextView = findViewById(R.id.role);

        // Assuming you have a users node in your Firebase Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        // Add a ValueEventListener to fetch user data
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user's name and role from the snapshot
                    String username = dataSnapshot.child("name").getValue().toString();
                    String role = dataSnapshot.child("role").getValue().toString();
                    // Set the retrieved values in the TextViews
                    usernameTextView.setText(username);
                    roleTextView.setText("Role: " + role);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
        recievedActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecievedActivity.class);
                startActivity(intent);
            }
        });
        analyseSpringBoundaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchUsingId.class);
                startActivity(intent);
            }
        });
        Analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Analytics.class);
                startActivity(intent);
            }
        });
        springPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SpringPlotsMapsActivity.class);
                startActivity(intent);
            }
        });
        SearchSpringData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchSpringshedData.class);
                startActivity(intent);
            }
        });
    }
}