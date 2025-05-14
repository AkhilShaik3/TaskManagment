package com.example.andrroidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    List<User> userList;

    public UserAdapter(List<User> list) {
        this.userList = list;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, userRole;

        public UserViewHolder(View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_layout, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userEmail.setText("Email: " + user.email);
        holder.userRole.setText("Role: " + user.role);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
