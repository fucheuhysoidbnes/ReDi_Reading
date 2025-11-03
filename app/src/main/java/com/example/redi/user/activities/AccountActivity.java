package com.example.redi.user.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.auth.LoginActivity;
import com.example.redi.common.base.BaseUserActivity;
import com.example.redi.common.models.User;
import com.example.redi.user.fragments.UpdateAccountFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AccountActivity extends BaseUserActivity {

    private ImageView ivAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvAddress, tvTotalSpent;
    private Button btnEditInfo, btnLogout;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_account_main);
        setupBottomNavigation(R.id.menu_account);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        checkLoginStatus();
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     **/
    private void checkLoginStatus() {
        if (auth.getCurrentUser() == null) {
            // ❌ Chưa đăng nhập
            isLoggedIn = false;
            showGuestLayout();
        } else {
            // ✅ Đã đăng nhập
            isLoggedIn = true;
            loadUserInfo(auth.getCurrentUser().getUid());
        }
    }

    /**
     * Giao diện khi chưa đăng nhập
     **/
    private void showGuestLayout() {
        ivAvatar.setImageResource(R.drawable.ic_account);

        btnEditInfo.setEnabled(false);
        btnEditInfo.setAlpha(0.5f); // làm mờ nút không bấm được

        btnLogout.setText("Đăng nhập");
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Load thông tin người dùng khi đã đăng nhập
     **/
    private void loadUserInfo(String userId) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    tvFullName.setText(user.getName());
                    tvEmail.setText("Email: " + user.getEmail());
                    tvPhone.setText("Số điện thoại: " + user.getPhone());
                    tvAddress.setText("Địa chỉ: " + user.getAddress());

                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(AccountActivity.this)
                                .load(user.getAvatarUrl())
                                .into(ivAvatar);
                    } else {
                        ivAvatar.setImageResource(R.drawable.ic_account);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Đã đăng nhập thì bật nút
        btnEditInfo.setEnabled(true);
        btnEditInfo.setAlpha(1f);

        // Nút Cập nhật thông tin → mở fragment UpdateAccountFragment
        btnEditInfo.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerUser, new UpdateAccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Nút Đăng xuất → xác nhận + đăng xuất
        btnLogout.setText("Đăng xuất");
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * Hộp thoại xác nhận đăng xuất
     **/
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    auth.signOut();
                    startActivity(new Intent(this, MainUserActivity.class));
                    finish();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    public void reloadUserData() {
        if (auth != null && auth.getCurrentUser() != null) {
            loadUserInfo(auth.getCurrentUser().getUid());
        }
    }

}

