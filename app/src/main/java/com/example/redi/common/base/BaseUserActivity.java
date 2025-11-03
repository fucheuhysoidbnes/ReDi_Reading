package com.example.redi.common.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redi.R;
import com.example.redi.user.activities.AccountActivity;
import com.example.redi.user.activities.CartActivity;
import com.example.redi.user.activities.MainUserActivity;
import com.example.redi.user.activities.OrdersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseUserActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** Gọi hàm này trong các Activity con sau khi setContentView() */
    protected void setupBottomNavigation(int currentMenuId) {
        bottomNav = findViewById(R.id.bottomNavigationUser);
        if (bottomNav == null) return;

        // Set item hiện tại (giúp tab đang chọn sáng)
        bottomNav.setSelectedItemId(currentMenuId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == currentMenuId) return true; // tránh reload lại trang đang ở

            Intent intent = null;

            if (itemId == R.id.menu_home) {
                intent = new Intent(this, MainUserActivity.class);
            } else if (itemId == R.id.menu_cart) {
                intent = new Intent(this, CartActivity.class);
            } else if (itemId == R.id.menu_orders) {
                intent = new Intent(this, OrdersActivity.class);
            } else if (itemId == R.id.menu_account) {
                intent = new Intent(this, AccountActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish(); // đóng Activity cũ để không chồng stack
            }

            return true;
        });
    }
}
