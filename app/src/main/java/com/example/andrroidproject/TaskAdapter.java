package com.example.andrroidproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.*;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    List<Task> taskList;
    Context context;

    public TaskAdapter(List<Task> tasks) {
        this.taskList = tasks;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskStatus, taskCommentPreview;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskCommentPreview = itemView.findViewById(R.id.taskCommentPreview);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.title);
        holder.taskStatus.setText("Status: " + task.status);


        switch (task.status) {
            case "Pending":
                holder.taskStatus.setTextColor(Color.RED);
                break;
            case "In Progress":
                holder.taskStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
                break;
            case "Completed":
                holder.taskStatus.setTextColor(Color.GREEN);
                break;
        }


        DatabaseReference commentRef = FirebaseDatabase.getInstance()
                .getReference("tasks")
                .child(task.taskId)
                .child("comments");

        commentRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Comment lastComment = snap.getValue(Comment.class);
                        holder.taskCommentPreview.setText("💬 " + lastComment.userName + ": " + lastComment.text);
                    }
                } else {
                    holder.taskCommentPreview.setText("💬 No comments yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.taskCommentPreview.setText("");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, TaskDetailActivity.class);
            i.putExtra("taskId", task.taskId);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
