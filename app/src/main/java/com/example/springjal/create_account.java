package com.example.springjal;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull; // For AndroidX library

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class create_account extends AppCompatActivity {

    // Firebase Authentication instance
    private FirebaseAuth mAuth;
    EditText nameInput,emailInput,phoneInput,passwordInput;
    RelativeLayout createAccountbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account); // Replace with your layout file

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the user type (role) passed from the previous activity
        String userType = getIntent().getStringExtra("USER_TYPE");

        // Find EditText fields
        nameInput = findViewById(R.id.nameinput);
        emailInput = findViewById(R.id.emailinput);
        phoneInput = findViewById(R.id.phoneinout);
        passwordInput = findViewById(R.id.passwdinput);
        createAccountbtn = findViewById(R.id.createaccountbtn);
        // Find the "Create Account" button and set an onClickListener
        createAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String userType = getIntent().getStringExtra("USER_TYPE");

                // Perform input validation here if needed

                // Create user with email and password using Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(create_account.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Store user details including role in Firebase Database (Realtime Database or Firestore)
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                        // Store user role
                                        usersRef.child(user.getUid()).child("role").setValue(userType);
                                        // Store other user details
                                        usersRef.child(user.getUid()).child("name").setValue(name);
                                        usersRef.child(user.getUid()).child("phone").setValue(phone);

                                        // Get the FCM token
                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                            String fcmToken = task.getResult();
                                                            // Store FCM token
                                                            usersRef.child(user.getUid()).child("fcmToken").setValue(fcmToken);
                                                        } else {
                                                            // Handle error
                                                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                                                        }
                                                    }
                                                });

                                        // Redirect the user to their specific home page based on role
                                        if (userType.equals("Data Collector")) {
                                            startActivity(new Intent(create_account.this, DataCollector_Home.class));
                                        } else if (userType.equals("Data Approver")) {
                                            startActivity(new Intent(create_account.this, DataApprover_Home.class));
                                        } else if (userType.equals("Admin")) {
                                            startActivity(new Intent(create_account.this, Admin_Home.class));
                                        } else {
                                            //Handle other roles or unknown roles
                                            Toast.makeText(create_account.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                        }
                                        finish(); // Close the current activity
                                        // Display a toast message for successful account creation
                                        Toast.makeText(create_account.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(create_account.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }); // <-- Closing parenthesis added here
    }
}
