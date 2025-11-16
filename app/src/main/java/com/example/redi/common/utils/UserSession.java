package com.example.redi.common.utils;

import android.content.Context;
import com.example.redi.common.models.User;

public class UserSession {
    private final SessionManager session;
    private final AppCache cache;

    public UserSession(Context context) {
        session = new SessionManager(context);
        cache = AppCache.getInstance();
    }

    /**  Lưu user vào cả cache và SharedPreferences */
    public void saveUser(User user) {
        if (user == null) return;
        cache.setCurrentUser(user);
        session.saveLogin(user.getId(), user.getEmail(), user.getRole());
        SessionObserver.getInstance().notifyChange();
    }

    /** Lấy user hiện tại: ưu tiên cache, fallback SharedPref */
    public User getCurrentUser() {
        if (cache.hasUser()) {
            return cache.getCurrentUser();
        }

        if (session.isLoggedIn()) {
            User u = new User();
            u.setId(session.getUserId());
            u.setEmail(session.getEmail());
            u.setRole(session.getRole());
            return u;
        }
        return null;
    }

    /** Kiểm tra login */
    public boolean isLoggedIn() {
        return session.isLoggedIn();
    }

    /** Đăng xuất toàn hệ thống */
    public void logout() {
        cache.clear();
        session.logout();
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
        SessionObserver.getInstance().notifyChange();
    }
}
