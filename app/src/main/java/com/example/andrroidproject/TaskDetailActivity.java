package com.example.andrroidproject;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class TaskDetailActivity extends AppCompatActivity {

    TextView taskTitle, taskDescription, taskDates, taskAssignedTo;
    Spinner statusSpinner;
    EditText commentInput;
    Button submitCommentButton;
    RecyclerView commentRecyclerView;

    String taskId;
    String currentUid;
    Task task;
    List<Comment> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;

    DatabaseReference taskRef, commentRef;
    FirebaseAuth mAuth;
    boolean isUserAssigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        taskDates = findViewById(R.id.taskDates);
        taskAssignedTo = findViewById(R.id.taskAssignedTo);
        statusSpinner = findViewById(R.id.statusSpinner);
        commentInput = findViewById(R.id.commentInput);
        submitCommentButton = findViewById(R.id.submitCommentButton);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        taskId = getIntent().getStringExtra("taskId");

        taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);
        commentRef = taskRef.child("comments");

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        setupStatusSpinner();
        loadTaskDetails();
        loadComments();

        submitCommentButton.setOnClickListener(v -> postComment());

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (task != null && isUserAssigned) {
                    String newStatus = parent.getItemAtPosition(pos).toString();
                    if (!task.status.equals(newStatus)) {
                        taskRef.child("status").setValue(newStatus);
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupStatusSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }

    private void loadTaskDetails() {
        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                task = snapshot.getValue(Task.class);
                if (task == null) return;

                isUserAssigned = task.assignedTo.equals(currentUid);

                taskTitle.setText(task.title);
                taskDescription.setText(task.description);
                taskDates.setText("Created: " + getDate(task.timestamp) + "\nDue: " + task.dueDate);
                taskAssignedTo.setText("Assigned To: " + task.assignedTo);


                statusSpinner.setSelection(((ArrayAdapter) statusSpinner.getAdapter()).getPosition(task.status));
                statusSpinner.setEnabled(isUserAssigned); // Only editable by assigned user
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void loadComments() {
        commentRef.orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        commentList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Comment comment = snap.getValue(Comment.class);
                            commentList.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void postComment() {
        String text = commentInput.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = commentRef.push().getKey();
        String name = mAuth.getCurrentUser().getEmail();
        long timestamp = System.currentTimeMillis();

        Comment comment = new Comment(currentUid, name, text, timestamp);
        commentRef.child(commentId).setValue(comment);
        commentInput.setText("");
    }

    private String getDate(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(millis));
    }
}
