package com.example.andrroidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    List<Comment> commentList;

    public CommentAdapter(List<Comment> list) {
        this.commentList = list;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUser, commentText, commentTime;

        public CommentViewHolder(View view) {
            super(view);
            commentUser = view.findViewById(R.id.commentUser);
            commentText = view.findViewById(R.id.commentContent);
            commentTime = view.findViewById(R.id.commentTimestamp);
        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment c = commentList.get(position);
        holder.commentUser.setText(c.userName);
        holder.commentText.setText(c.text);
        holder.commentTime.setText(DateFormat.getDateTimeInstance().format(c.timestamp));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
