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
        // Nếu cache đã có, trả ngay để UI hiển thị tức thì
        if (AppCache.getInstance().hasBooks()) {
            callback.onSuccess(AppCache.getInstance().getBooks());
        }

        // Tiếp tục tải từ Firebase để cập nhật mới nhất
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    if (book != null) {
                        String contentUrl = book.getContent();

                        if (contentUrl != null && contentUrl.contains("drive.google.com")) {
                            try {
                                String id = null;

                                // Dạng /file/d/ID/
                                if (contentUrl.contains("/file/d/")) {
                                    id = contentUrl.split("/d/")[1].split("/")[0];
                                }
                                //  Dạng ?id=ID
                                else if (contentUrl.contains("id=")) {
                                    id = contentUrl.split("id=")[1].split("&")[0];
                                }

                                // Nếu có ID thì chuyển sang link nhúng Drive
                                if (id != null && !id.isEmpty()) {
                                    String fixedUrl = "https://drive.google.com/file/d/" + id + "/preview";
                                    book.setContent(fixedUrl);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        list.add(book);
                    }
                }

                // Lưu cache để tái sử dụng
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
