package com.example.redi.data.firebase;

import androidx.annotation.NonNull;
import com.example.redi.common.models.Book;
import com.example.redi.common.utils.AppCache;
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
        // ✅ Nếu cache đã có, trả ngay để UI hiển thị tức thì
        if (AppCache.getInstance().hasBooks()) {
            callback.onSuccess(AppCache.getInstance().getBooks());
        }

        // ✅ Tiếp tục tải từ Firebase để cập nhật mới nhất
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    if (book != null) {
                        // 🔧 Fix tự động link Drive sai
                        if (book.getContent() != null && book.getContent().contains("drive.google.com/file/d/")) {
                            try {
                                String id = book.getContent().split("/d/")[1].split("/")[0];
                                String fixedUrl = "https://drive.google.com/uc?export=preview&id=" + id;
                                book.setContent(fixedUrl);
                            } catch (Exception ignored) {}
                        }

                        list.add(book);
                    }
                }

                // ✅ Lưu cache để tái sử dụng
                AppCache.getInstance().setBooks(list);
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
