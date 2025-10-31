package com.example.redi.data.firebase;

import com.example.redi.common.models.User;
import com.google.firebase.database.*;

public class FirebaseUserDataSource {
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        usersRef.child(user.getId()).setValue(user, listener);
    }

    public void getUserRole(String uid, ValueEventListener listener) {
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(listener);
    }
}