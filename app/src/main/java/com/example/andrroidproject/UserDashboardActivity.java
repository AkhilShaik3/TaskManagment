package com.example.andrroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {

    RecyclerView userTaskRecyclerView;
    Button logoutButton, viewTasksButton, markCompletedButton;

    List<Task> userTaskList;
    TaskAdapter taskAdapter;
    FirebaseAuth firebaseAuth;
    DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        logoutButton = findViewById(R.id.logoutButton);
        viewTasksButton = findViewById(R.id.viewTasksButton);
        markCompletedButton = findViewById(R.id.markCompletedButton);
        userTaskRecyclerView = findViewById(R.id.userTaskRecyclerView);

        userTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userTaskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(userTaskList);
        userTaskRecyclerView.setAdapter(taskAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        taskRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadUserTasks();

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        viewTasksButton.setOnClickListener(v -> loadUserTasks());

        markCompletedButton.setOnClickListener(v -> {
            String uid = firebaseAuth.getCurrentUser().getUid();
            taskRef.orderByChild("assignedTo").equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                taskRef.child(snap.getKey()).child("status").setValue("Completed");
                            }
                            Toast.makeText(UserDashboardActivity.this, "All tasks marked as completed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });
        });
    }

    private void loadUserTasks() {
        String uid = firebaseAuth.getCurrentUser().getUid();

        taskRef.orderByChild("assignedTo").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        userTaskList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            userTaskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(UserDashboardActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
