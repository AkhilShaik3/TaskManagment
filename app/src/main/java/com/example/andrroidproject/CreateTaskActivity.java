package com.example.andrroidproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateTaskActivity extends AppCompatActivity {

    EditText titleEditText, descriptionEditText, commentEditText, dueDateEditText;
    Spinner userSpinner;
    Button createTaskButton;

    FirebaseAuth mAuth;
    DatabaseReference usersRef, tasksRef;
    ArrayList<String> userEmails = new ArrayList<>();
    Map<String, String> emailToUidMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        commentEditText = findViewById(R.id.commentEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        userSpinner = findViewById(R.id.userSpinner);
        createTaskButton = findViewById(R.id.createTaskButton);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadUsers();

        dueDateEditText.setOnClickListener(v -> showDatePicker());

        createTaskButton.setOnClickListener(v -> createTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            dueDateEditText.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadUsers() {
        usersRef.orderByChild("role").equalTo("user")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userEmails.clear();
                        emailToUidMap.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String email = snap.child("email").getValue(String.class);
                            String uid = snap.getKey();
                            if (email != null && uid != null) {
                                userEmails.add(email);
                                emailToUidMap.put(email, uid);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateTaskActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, userEmails);
                        userSpinner.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void createTask() {
        String title = titleEditText.getText().toString().trim();
        String desc = descriptionEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();
        String dueDate = dueDateEditText.getText().toString().trim();
        String userEmail = userSpinner.getSelectedItem() != null ? userSpinner.getSelectedItem().toString() : "";
        String assignedTo = emailToUidMap.get(userEmail);
        String createdBy = mAuth.getCurrentUser().getUid();
        String taskId = tasksRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        if (title.isEmpty() || desc.isEmpty() || dueDate.isEmpty() || assignedTo == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        Task task = new Task(taskId, title, desc, dueDate, "Medium", "Pending", createdBy, assignedTo, timestamp);
        tasksRef.child(taskId).setValue(task);

        if (!comment.isEmpty()) {
            DatabaseReference commentRef = tasksRef.child(taskId).child("comments").push();
            Comment commentObj = new Comment(createdBy, mAuth.getCurrentUser().getEmail(), comment, timestamp);
            commentRef.setValue(commentObj);
        }

        Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
