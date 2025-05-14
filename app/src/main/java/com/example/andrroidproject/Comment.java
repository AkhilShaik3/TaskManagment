package com.example.andrroidproject;

public class Comment {
    public String commentId, userId, userName, text;
    public long timestamp;

    public Comment() {}

    public Comment(String commentId, String userId, String userName, String text, long timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.timestamp = timestamp;
    }
}
