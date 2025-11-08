package com.example.redi.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton để theo dõi thay đổi đăng nhập / đăng xuất / cập nhật user
 * Các Activity hoặc Fragment có thể đăng ký để tự reload khi user thay đổi
 */
public class SessionObserver {

    public interface Listener {
        void onUserSessionChanged();
    }

    private static SessionObserver instance;
    private final Set<Listener> listeners = new HashSet<>();

    private SessionObserver() {}

    public static synchronized SessionObserver getInstance() {
        if (instance == null) instance = new SessionObserver();
        return instance;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /** Gọi hàm này khi user đăng nhập / đăng xuất / update */
    public void notifyChange() {
        for (Listener l : listeners) {
            l.onUserSessionChanged();
        }
    }
}
