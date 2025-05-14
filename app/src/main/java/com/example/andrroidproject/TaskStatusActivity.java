package com.example.andrroidproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class TaskStatusActivity extends AppCompatActivity {

    RecyclerView taskRecyclerView;
    TaskAdapter taskAdapter;
    List<Task> allTasks;
    DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_status);

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        allTasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(allTasks);
        taskRecyclerView.setAdapter(taskAdapter);

        taskRef = FirebaseDatabase.getInstance().getReference("tasks");

        loadAllTasks();
    }

    private void loadAllTasks() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allTasks.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Task task = snap.getValue(Task.class);
                    allTasks.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TaskStatusActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
