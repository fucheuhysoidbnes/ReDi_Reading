package com.example.redi.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserVH> {

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    private final List<User> users = new ArrayList<>();
    private final OnUserActionListener listener;

    public AdminUserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> list) {
        users.clear();
        if (list != null) users.addAll(list);
        notifyDataSetChanged();
    }

    public User getUserAt(int pos) {
        return users.get(pos);
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User u = users.get(position);

        holder.tvName.setText(u.getName());
        holder.tvEmail.setText(u.getEmail());
        holder.tvRole.setText(u.getRole() == null ? "user" : u.getRole());

        // ------------------------
        // Load ảnh bằng GLIDE
        // ------------------------
        if (u.getAvatarUrl() != null && !u.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(u.getAvatarUrl())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_account);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(u));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(u));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserVH extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvEmail, tvRole;
        ImageView btnEdit, btnDelete;

        UserVH(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tv_role);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
