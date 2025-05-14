package com.example.andrroidproject;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailRegister, passwordRegister, confirmPasswordRegister;
    RadioGroup roleRadioGroup;
    RadioButton selectedRoleButton;
    Button registerButton, loginRedirectButton;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        registerButton = findViewById(R.id.registerButton);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {
            String email = emailRegister.getText().toString().trim();
            String password = passwordRegister.getText().toString().trim();
            String confirmPassword = confirmPasswordRegister.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailRegister.setError("Invalid email");
                return;
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                passwordRegister.setError("Password must be 6+ chars");
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordRegister.setError("Passwords do not match");
                return;
            }

            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedRoleButton = findViewById(selectedId);
            String role = selectedRoleButton.getText().toString().toLowerCase();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        User user = new User(uid, email, role);
                        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(user);
                        firebaseAuth.signOut(); // Redirect to login after registration
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        loginRedirectButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }
}
