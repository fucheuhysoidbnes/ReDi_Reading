package com.example.redi.data.repository;

import com.example.redi.common.models.Book;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.firebase.FirebaseBookDataSource;

import java.util.List;

public class BookRepository {
    private final FirebaseBookDataSource dataSource;

    public BookRepository() {
        this.dataSource = new FirebaseBookDataSource();
    }

    public void getBooks(DataSourceCallback<List<Book>> callback) {
        dataSource.getAllBooks(callback);
    }
}