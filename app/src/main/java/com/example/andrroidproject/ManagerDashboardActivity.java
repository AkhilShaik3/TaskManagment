package com.example.andrroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ManagerDashboardActivity extends AppCompatActivity {

    RecyclerView managerTaskRecyclerView;
    Button createTaskButton, logoutButton, viewUsersButton, viewAssignedTasksButton;

    List<Task> taskList = new ArrayList<>();
    TaskAdapter taskAdapter;
    DatabaseReference taskRef;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        managerTaskRecyclerView = findViewById(R.id.managerTaskRecyclerView);
        createTaskButton = findViewById(R.id.createTaskButton);
        logoutButton = findViewById(R.id.logoutButton);
        viewUsersButton = findViewById(R.id.viewUsersButton);
        viewAssignedTasksButton = findViewById(R.id.viewAssignedTasksButton);

        firebaseAuth = FirebaseAuth.getInstance();
        taskRef = FirebaseDatabase.getInstance().getReference("tasks");

        managerTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        managerTaskRecyclerView.setAdapter(taskAdapter);

        loadManagerTasks();

        createTaskButton.setOnClickListener(v ->
                startActivity(new Intent(this, CreateTaskActivity.class)));

        viewUsersButton.setOnClickListener(v ->
                startActivity(new Intent(this, UserListActivity.class)));

        viewAssignedTasksButton.setOnClickListener(v ->
                startActivity(new Intent(this, TaskStatusActivity.class)));

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadManagerTasks() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        taskRef.orderByChild("createdBy").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        taskList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            taskList.add(task);
                        }


                        Collections.sort(taskList, (t1, t2) -> {
                            List<String> order = Arrays.asList("In Progress", "Pending", "Completed");
                            return Integer.compare(order.indexOf(t1.status), order.indexOf(t2.status));
                        });

                        taskAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}
