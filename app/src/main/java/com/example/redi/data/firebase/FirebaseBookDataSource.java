package com.example.redi.data.firebase;

import androidx.annotation.NonNull;
import com.example.redi.common.models.Book;
import com.example.redi.data.DataSourceCallback;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class FirebaseBookDataSource {
    private final DatabaseReference booksRef;

    public FirebaseBookDataSource() {
        booksRef = FirebaseDatabase.getInstance().getReference("books");
    }

    public void getAllBooks(DataSourceCallback<List<Book>> callback) {
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    list.add(book);
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