package com.example.andrroidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    List<Task> taskList;

    public TaskAdapter(List<Task> list) {
        this.taskList = list;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDueDate, taskStatus;

        public TaskViewHolder(View v) {
            super(v);
            taskTitle = v.findViewById(R.id.taskTitle);
            taskDueDate = v.findViewById(R.id.taskDueDate);
            taskStatus = v.findViewById(R.id.taskStatus);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.title);
        holder.taskDueDate.setText("Due: " + task.dueDate);
        holder.taskStatus.setText("Status: " + task.status);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
