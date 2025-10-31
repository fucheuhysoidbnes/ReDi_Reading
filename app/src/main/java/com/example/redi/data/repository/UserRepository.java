package com.example.redi.data.repository;

import com.example.redi.common.models.User;
import com.example.redi.data.firebase.FirebaseUserDataSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserRepository {
    private final FirebaseUserDataSource ds = new FirebaseUserDataSource();

    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        ds.saveUser(user, listener);
    }

    public void getUserRole(String uid, ValueEventListener listener) {
        ds.getUserRole(uid, listener);
    }
}