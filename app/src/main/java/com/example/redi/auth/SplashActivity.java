package com.example.redi.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redi.R;
import com.example.redi.admin.activities.MainAdminActivity;
import com.example.redi.common.models.Book;
import com.example.redi.common.models.User;
import com.example.redi.common.utils.AppCache;
import com.example.redi.common.utils.UserSession;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.BookRepository;
import com.example.redi.data.repository.UserRepository;
import com.example.redi.user.activities.MainUserActivity;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 2.5s hiệu ứng khởi động
    private final BookRepository bookRepo = new BookRepository();
    private final UserRepository userRepo = new UserRepository();
    private UserSession userSession;

    private boolean booksLoaded = false;
    private boolean userLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_zoom);
        logo.startAnimation(anim);

        userSession = new UserSession(this);

        // ✅ Load song song dữ liệu sách + user
        preloadBooks();
        preloadUser();

        // ✅ Sau SPLASH_DELAY giây → chuyển màn hình (kể cả khi dữ liệu chưa xong)
        new Handler().postDelayed(this::goNextScreen, SPLASH_DELAY);
    }

    // ===================== PRELOAD BOOKS ===================== //
    private void preloadBooks() {
        bookRepo.getBooks(new DataSourceCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> data) {
                AppCache.getInstance().setBooks(data);
                booksLoaded = true;
                System.out.println("✅ Books preloaded: " + data.size());
            }

            @Override
            public void onError(String error) {
                booksLoaded = true; // vẫn cho phép vào app
                System.err.println("⚠️ Preload books error: " + error);
            }
        });
    }

    // ===================== PRELOAD USER ===================== //
    private void preloadUser() {
        User cached = userSession.getCurrentUser();
        if (cached == null || cached.getId() == null) {
            userLoaded = true;
            System.out.println("⚠️ No cached user -> skip preload user");
            return;
        }

        // Lấy user mới nhất từ Firebase để cập nhật cache
        userRepo.getUserById(cached.getId(), new DataSourceCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    userSession.saveUser(user);
                    System.out.println("✅ User preloaded: " + user.getEmail());
                }
                userLoaded = true;
            }

            @Override
            public void onError(String error) {
                userLoaded = true;
                System.err.println("⚠️ Preload user error: " + error);
            }
        });
    }

    // ===================== NAVIGATION ===================== //
    private void goNextScreen() {
        // Nếu dữ liệu tải chưa xong, vẫn cho qua để tránh delay
        if (!booksLoaded || !userLoaded) {
            System.out.println("ℹ️ Dữ liệu chưa tải xong hoàn toàn nhưng tiếp tục...");
        }

        Intent intent;

        if (userSession.isLoggedIn()) {
            User current = userSession.getCurrentUser();

            if (current != null && "admin".equals(current.getRole())) {
                intent = new Intent(this, MainAdminActivity.class);
            } else {
                intent = new Intent(this, MainUserActivity.class);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
