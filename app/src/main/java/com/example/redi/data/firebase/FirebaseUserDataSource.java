package com.example.redi.data.firebase;

import androidx.annotation.NonNull;

import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;

public class FirebaseUserDataSource {
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    // ✅ Lưu user vào Realtime Database
    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        usersRef.child(user.getId()).setValue(user, listener);
    }

    // ✅ Lấy role riêng (dùng cho login)
    public void getUserRole(String uid, ValueEventListener listener) {
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(listener);
    }

    // ✅ Lấy toàn bộ thông tin user theo ID (dùng cho preload hoặc account info)
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
}
