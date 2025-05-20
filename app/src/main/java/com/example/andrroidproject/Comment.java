package com.example.andrroidproject;



public class Comment {
    public String userId, userName, text;
    public long timestamp;

    public Comment() {}

    public Comment(String userId, String userName, String text, long timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.timestamp = timestamp;
    }
}
