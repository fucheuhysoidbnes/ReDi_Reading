package com.example.redi.admin.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.redi.common.models.Book;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.BookRepository;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.UUID;

public class AdminBookViewModel extends ViewModel {

    private final BookRepository repo = new BookRepository();
    public MutableLiveData<List<Book>> bookList = new MutableLiveData<>();

    public void loadBooks() {
        repo.getBooks(new DataSourceCallback<List<Book>>() {
            @Override public void onSuccess(List<Book> data) {
                bookList.postValue(data);
            }
            @Override public void onError(String error) {}
        });
    }

    public void addBook(Book b) {
        String id = b.getBook_id();
        if (id == null || id.isEmpty()) id = UUID.randomUUID().toString();
        b.setBook_id(id);

        FirebaseDatabase.getInstance().getReference("books")
                .child(id).setValue(b);

        loadBooks();
    }

    public void updateBook(Book b) {
        FirebaseDatabase.getInstance().getReference("books")
                .child(b.getBook_id()).setValue(b);
        loadBooks();
    }

    public void deleteBook(String id) {
        FirebaseDatabase.getInstance().getReference("books")
                .child(id).removeValue();
        loadBooks();
    }
}

