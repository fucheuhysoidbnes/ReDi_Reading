package com.example.redi.data.firebase;

import androidx.annotation.NonNull;

import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserDataSource {

    private final DatabaseReference usersRef =
            FirebaseDatabase.getInstance().getReference("users");

    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        if (user.getId() == null) return;
        usersRef.child(user.getId()).setValue(user, listener);
    }

    public void deleteUser(String uid, DatabaseReference.CompletionListener listener) {
        if (uid == null) return;
        usersRef.child(uid).removeValue(listener);
    }

    public void getUserRole(String uid, ValueEventListener listener) {
        if (uid == null) return;
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(listener);
    }

    public void getUserById(String uid, DataSourceCallback<User> callback) {
        if (uid == null) {
            callback.onError("UID null");
            return;
        }

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if (u != null) callback.onSuccess(u);
                else callback.onError("User not found");
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void getAllUsers(DataSourceCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User u = s.getValue(User.class);
                    if (u != null) list.add(u);
                }
                callback.onSuccess(list);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void searchUsersByEmail(String email, DataSourceCallback<List<User>> callback) {
        Query q = usersRef.orderByChild("email")
                .startAt(email)
                .endAt(email + "\uf8ff");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> list = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    User u = s.getValue(User.class);
                    if (u != null) list.add(u);
                }
                callback.onSuccess(list);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
