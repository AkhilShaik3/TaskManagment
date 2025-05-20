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

public class UserDashboardActivity extends AppCompatActivity {

    RecyclerView userTaskRecyclerView;
    Button logoutButton;

    List<Task> userTaskList;
    TaskAdapter taskAdapter;
    FirebaseAuth firebaseAuth;
    DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        logoutButton = findViewById(R.id.logoutButton);
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

                        // Sort by status: In Progress > Pending > Completed
                        Collections.sort(userTaskList, (t1, t2) -> {
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
