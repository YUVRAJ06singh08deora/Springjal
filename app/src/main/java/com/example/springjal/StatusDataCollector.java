package com.example.springjal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatusDataCollector extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StatusAdapter activityAdapter;
    private List<StatusActivityModel> activityList;
    private loading loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_data_collector);

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recycler_view_activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show loading dialog
        loadingDialog = new loading(StatusDataCollector.this);
        loadingDialog.show();

        // Fetch activities from Firestore
        fetchActivitiesFromFirestore();

        // Initialize adapter with an empty list (will be updated later)
        activityList = new ArrayList<>();
        activityAdapter = new StatusAdapter(activityList, this);
        recyclerView.setAdapter(activityAdapter);
    }

    // Fetch activities from Firestore
    private void fetchActivitiesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("activities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String activityId = documentSnapshot.getId(); // Get the unique activity ID
                        String approvalStatus = documentSnapshot.getString("approvalStatus");

                        // Create a StatusActivityModel object with the activity ID as its name
                        StatusActivityModel activity = new StatusActivityModel(activityId, approvalStatus);

                        // Add the activity to the list
                        activityList.add(activity);
                    }

                    // Update the adapter with the new data
                    activityAdapter.notifyDataSetChanged();

                    // Hide loading dialog after data is fetched
                    loadingDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    // Handle failure when fetching data from Firestore
                    Toast.makeText(getApplicationContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Hide loading dialog in case of failure
                    loadingDialog.dismiss();
                });
    }
}
