package com.example.andrroidproject;




public class Task {
    public String taskId, title, description, dueDate, priority, status, createdBy, assignedTo;
    public long timestamp;

    public Task() {}

    public Task(String taskId, String title, String description, String dueDate,
                String priority, String status, String createdBy, String assignedTo, long timestamp) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.timestamp = timestamp;
    }
}
