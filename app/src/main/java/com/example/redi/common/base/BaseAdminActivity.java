package com.example.redi.common.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.redi.R;
import com.example.redi.admin.activities.MainAdminActivity;
import com.example.redi.admin.activities.ManageBookActivity;
import com.example.redi.admin.activities.ManageOrdersActivity;
import com.example.redi.admin.activities.ManageAccountActivity;
import com.example.redi.common.utils.UserSession;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseAdminActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_base);

        userSession = new UserSession(this);

        setupDrawer();
        setupMenuEvents();
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.admin_drawer);
        navigationView = findViewById(R.id.admin_navigation);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);


        toolbar.setTitle("Trang quản trị");
        // hoặc getSupportActionBar().setTitle("Admin Panel");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }



    private void setupMenuEvents() {
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_home) {
                startActivity(new Intent(this, MainAdminActivity.class));
            }
            else if (id == R.id.menu_books) {
                startActivity(new Intent(this, ManageBookActivity.class));
            }
            else if (id == R.id.menu_orders) {
                startActivity(new Intent(this, ManageOrdersActivity.class));
            }
            else if (id == R.id.menu_users) {
                startActivity(new Intent(this, ManageAccountActivity.class));
            }
            else if (id == R.id.menu_logout) {
                showLogoutConfirm();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void showLogoutConfirm() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dlg, which) -> {
                    userSession.logout();
                    Intent intent = new Intent(this, com.example.redi.auth.LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
