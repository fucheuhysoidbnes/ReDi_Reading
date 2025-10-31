package com.example.redi.user.viewmodel;

import com.example.redi.common.models.Book;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.BookRepository;

import java.util.List;

public class BookViewModel {
    private final BookRepository repository = new BookRepository();

    public void loadBooks(DataSourceCallback<List<Book>> callback) {
        repository.getBooks(callback);
    }
}