package com.example.redi.data.firebase;

import androidx.annotation.NonNull;

import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserDataSource {
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    // Lưu user (create or update)
    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        usersRef.child(user.getId()).setValue(user, listener);
    }

    // Delete user
    public void deleteUser(String uid, DatabaseReference.CompletionListener listener) {
        usersRef.child(uid).removeValue(listener);
    }

    // Lấy role riêng (dùng cho login)
    public void getUserRole(String uid, ValueEventListener listener) {
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(listener);
    }

    // Lấy user theo ID
    public void getUserById(String uid, DataSourceCallback<User> callback) {
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) callback.onSuccess(user);
                else callback.onError("User not found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Lấy tất cả users
    public void getAllUsers(DataSourceCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User u = s.getValue(User.class);
                    if (u != null) list.add(u);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Search users by email (prefix match)
    public void searchUsersByEmail(String emailQuery, DataSourceCallback<List<User>> callback) {
        // Query orderByChild("email").startAt(emailQuery).endAt(emailQuery + "\uf8ff")
        Query q = usersRef.orderByChild("email")
                .startAt(emailQuery)
                .endAt(emailQuery + "\uf8ff");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User u = s.getValue(User.class);
                    if (u != null) list.add(u);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
