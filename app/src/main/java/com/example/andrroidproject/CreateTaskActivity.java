package com.example.andrroidproject;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class CreateTaskActivity extends AppCompatActivity {

    EditText titleEditText, descriptionEditText, dueDateEditText;
    RadioGroup priorityRadioGroup;
    Spinner userSpinner;
    Button submitTaskButton;

    DatabaseReference usersRef, taskRef;
    FirebaseAuth mAuth;

    ArrayList<String> userNames = new ArrayList<>();
    Map<String, String> uidMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        priorityRadioGroup = findViewById(R.id.priorityRadioGroup);
        userSpinner = findViewById(R.id.userSpinner);
        submitTaskButton = findViewById(R.id.submitTaskButton);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        taskRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadUsers();

        dueDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = day + "/" + (month + 1) + "/" + year;
                dueDateEditText.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        submitTaskButton.setOnClickListener(v -> createTask());
    }

    private void loadUsers() {
        usersRef.orderByChild("role").equalTo("user")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String name = snap.child("email").getValue(String.class);
                            String uid = snap.getKey();
                            uidMap.put(name, uid);
                            userNames.add(name);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateTaskActivity.this, android.R.layout.simple_spinner_dropdown_item, userNames);
                        userSpinner.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void createTask() {
        String title = titleEditText.getText().toString();
        String desc = descriptionEditText.getText().toString();
        String dueDate = dueDateEditText.getText().toString();
        int priorityId = priorityRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedPriority = findViewById(priorityId);
        String priority = selectedPriority.getText().toString();
        String assignedTo = uidMap.get(userSpinner.getSelectedItem().toString());
        String createdBy = mAuth.getUid();
        String taskId = taskRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        Task task = new Task(taskId, title, desc, dueDate, priority, "Pending", createdBy, assignedTo, timestamp);
        taskRef.child(taskId).setValue(task)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
