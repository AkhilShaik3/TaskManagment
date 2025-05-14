package com.example.andrroidproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton, registerRedirectButton;
    TextView forgotPasswordText;
    FirebaseAuth firebaseAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        registerRedirectButton = findViewById(R.id.registerRedirectButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("role");

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String role = snapshot.getValue(String.class);
                                if ("manager".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this, ManagerDashboardActivity.class));
                                } else if ("user".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid role", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(LoginActivity.this, "Failed to load role", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        forgotPasswordText.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email first", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Reset link sent", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        registerRedirectButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
