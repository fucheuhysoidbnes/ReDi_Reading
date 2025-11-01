package com.example.redi.common.utils;

import com.example.redi.common.models.Book;
import com.example.redi.common.models.User;

import java.util.ArrayList;
import java.util.List;

public class AppCache {

    private static AppCache instance;

    private final List<Book> cachedBooks = new ArrayList<>();
    private User currentUser;

    private AppCache() {}

    public static synchronized AppCache getInstance() {
        if (instance == null) instance = new AppCache();
        return instance;
    }

    // ===================== BOOK CACHE ===================== //
    public void setBooks(List<Book> books) {
        cachedBooks.clear();
        if (books != null) cachedBooks.addAll(books);
    }

    public List<Book> getBooks() {
        return cachedBooks;
    }

    public boolean hasBooks() {
        return !cachedBooks.isEmpty();
    }

    // ===================== USER CACHE ===================== //
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasUser() {
        return currentUser != null;
    }

    // ===================== CLEAR CACHE ===================== //
    public void clear() {
        cachedBooks.clear();
        currentUser = null;
    }
}
