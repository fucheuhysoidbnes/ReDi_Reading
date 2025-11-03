package com.example.redi.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.redi.common.models.User;

public class SessionManager {
    private static final String PREF_NAME = "redi_session";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ✅ Lưu thông tin đăng nhập đầy đủ
    public void saveUser(User user) {
        if (user == null) return;

        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.putString(KEY_AVATAR, user.getAvatarUrl());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // ✅ Lưu thông tin cơ bản khi chỉ có uid, email, role
    public void saveLogin(String uid, String email, String role) {
        editor.putString(KEY_USER_ID, uid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "user");
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public String getAddress() {
        return prefs.getString(KEY_ADDRESS, "");
    }

    public String getAvatarUrl() {
        return prefs.getString(KEY_AVATAR, "");
    }

    // ✅ Lấy toàn bộ user dưới dạng object
    public User getUser() {
        if (!isLoggedIn()) return null;

        User u = new User();
        u.setId(getUserId());
        u.setEmail(getEmail());
        u.setRole(getRole());
        u.setName(getName());
        u.setPhone(getPhone());
        u.setAddress(getAddress());
        u.setAvatarUrl(getAvatarUrl());
        return u;
    }

    // ✅ Đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
