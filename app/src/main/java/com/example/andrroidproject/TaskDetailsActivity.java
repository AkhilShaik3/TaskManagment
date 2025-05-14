package com.example.andrroidproject;

import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView detailTitle, detailDesc, detailDueDate;
    RadioGroup statusRadioGroup;
    EditText commentEditText;
    Button updateStatusButton, postCommentButton;
    RecyclerView commentRecyclerView;

    DatabaseReference taskRef, commentRef, userRef;
    FirebaseAuth mAuth;
    String taskId, currentUserId, currentUserName;
    List<Comment> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_details);

        taskId = getIntent().getStringExtra("taskId");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();

        detailTitle = findViewById(R.id.detailTitle);
        detailDesc = findViewById(R.id.detailDesc);
        detailDueDate = findViewById(R.id.detailDueDate);
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        commentEditText = findViewById(R.id.commentEditText);
        updateStatusButton = findViewById(R.id.updateStatusButton);
        postCommentButton = findViewById(R.id.postCommentButton);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);
        commentRef = taskRef.child("comments");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.child("name").get().addOnSuccessListener(snapshot ->
                currentUserName = snapshot.getValue(String.class));

        loadTaskDetails();
        loadComments();

        updateStatusButton.setOnClickListener(v -> updateTaskStatus());
        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadTaskDetails() {
        taskRef.get().addOnSuccessListener(snapshot -> {
            Task task = snapshot.getValue(Task.class);
            if (task != null) {
                detailTitle.setText(task.title);
                detailDesc.setText(task.description);
                detailDueDate.setText("Due: " + task.dueDate);
                switch (task.status) {
                    case "Pending": statusRadioGroup.check(R.id.statusPending); break;
                    case "In Progress": statusRadioGroup.check(R.id.statusInProgress); break;
                    case "Completed": statusRadioGroup.check(R.id.statusCompleted); break;
                }
            }
        });
    }

    private void updateTaskStatus() {
        int selectedId = statusRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);
        String newStatus = selectedRadio.getText().toString();
        taskRef.child("status").setValue(newStatus);
        Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
    }

    private void postComment() {
        String text = commentEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        String commentId = commentRef.push().getKey();
        long timestamp = System.currentTimeMillis();
        Comment comment = new Comment(commentId, currentUserId, currentUserName, text, timestamp);
        commentRef.child(commentId).setValue(comment);
        commentEditText.setText("");
    }

    private void loadComments() {
        commentRef.addValueEventListener(new ValueEventListener() {
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
}
