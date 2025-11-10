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

    /**
     * Lấy toàn bộ danh sách sách từ Firebase
     * - Nếu cache có thì trả ngay để UI hiển thị nhanh
     * - Sau đó vẫn tải mới từ Firebase để đảm bảo dữ liệu cập nhật
     */
    public void getAllBooks(DataSourceCallback<List<Book>> callback) {
        // Nếu cache đã có dữ liệu, trả luôn để hiển thị ngay
        if (AppCache.getInstance().hasBooks()) {
            callback.onSuccess(AppCache.getInstance().getBooks());
        }

        // Tiếp tục tải mới từ Firebase
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    if (book == null) continue;

                    //  Chuẩn hóa link ảnh (Google Drive)
                    String imageUrl = book.getImageUrl();
                    if (imageUrl != null && imageUrl.contains("drive.google.com")) {
                        String id = extractDriveId(imageUrl);
                        if (id != null && !id.isEmpty()) {
                            // Dạng link có thể load trực tiếp qua Glide
                            String fixedImageUrl = "https://drive.google.com/uc?export=view&id=" + id;
                            book.setImageUrl(fixedImageUrl);
                        }
                    }

                    // Chuẩn hóa link nội dung (Google Drive)
                    String contentUrl = book.getContent();
                    if (contentUrl != null && contentUrl.contains("drive.google.com")) {
                        String id = extractDriveId(contentUrl);
                        if (id != null && !id.isEmpty()) {
                            // Dạng link preview có thể mở bằng WebView (PDF, video, v.v.)
                            String fixedContentUrl = "https://drive.google.com/file/d/" + id + "/preview";
                            book.setContent(fixedContentUrl);
                        }
                    }

                    list.add(book);
                }

                // Lưu cache để dùng lại cho lần sau
                AppCache.getInstance().setBooks(list);
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    /**
     * Hàm phụ để trích xuất ID từ link Google Drive ở mọi dạng
     * Hỗ trợ các dạng:
     * 1. https://drive.google.com/file/d/ID/view?usp=sharing
     * 2. https://drive.google.com/open?id=ID
     * 3. https://drive.google.com/uc?id=ID&export=view
     * 4. https://drive.google.com/thumbnail?id=ID
     */
    private String extractDriveId(String url) {
        try {
            if (url.contains("/file/d/")) {
                return url.split("/d/")[1].split("/")[0];
            } else if (url.contains("id=")) {
                return url.split("id=")[1].split("&")[0];
            } else if (url.contains("/uc?export=view&id=")) {
                return url.split("id=")[1];
            }
        } catch (Exception ignored) {}
        return null;
    }
}
