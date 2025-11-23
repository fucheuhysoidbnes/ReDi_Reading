package com.example.redi.data.repository;

import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.firebase.FirebaseUserDataSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserRepository {

    private final FirebaseUserDataSource ds = new FirebaseUserDataSource();

    public void saveUser(User user, DatabaseReference.CompletionListener listener) {
        ds.saveUser(user, listener);
    }

    public void deleteUser(String uid, DatabaseReference.CompletionListener listener) {
        ds.deleteUser(uid, listener);
    }

    public void getUserRole(String uid, ValueEventListener listener) {
        ds.getUserRole(uid, listener);
    }

    public void getUserById(String uid, DataSourceCallback<User> callback) {
        ds.getUserById(uid, callback);
    }

    public void getAllUsers(DataSourceCallback<List<User>> callback) {
        ds.getAllUsers(callback);
    }

    public void searchUsersByEmail(String email, DataSourceCallback<List<User>> callback) {
        ds.searchUsersByEmail(email, callback);
    }
}
