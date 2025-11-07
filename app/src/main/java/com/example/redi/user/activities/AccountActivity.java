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
import com.example.redi.common.utils.AppCache;
import com.example.redi.common.utils.UserSession;
import com.example.redi.user.fragments.UpdateAccountFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AccountActivity extends BaseUserActivity {

    private ImageView ivAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvAddress;
    private Button btnEditInfo, btnLogout;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private UserSession userSession;

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
        userSession = new UserSession(this);

        checkLoginStatus();
    }

    /** Kiểm tra login và hiển thị */
    private void checkLoginStatus() {
        if (auth.getCurrentUser() == null || !userSession.isLoggedIn()) {
            showGuestLayout();
        } else {
            loadUserInfo(auth.getCurrentUser().getUid());
        }
    }

    /** Giao diện khách vãng lai */
    private void showGuestLayout() {
        ivAvatar.setImageResource(R.drawable.ic_account);
        tvFullName.setText("Khách vãng lai");
        tvEmail.setText("Email");
        tvPhone.setText("Số điện thoại");
        tvAddress.setText("Địa chỉ");

        btnEditInfo.setEnabled(false);
        btnEditInfo.setAlpha(0.5f);

        btnLogout.setText("Đăng nhập");
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /** 🔹 Load thông tin từ Firebase */
    private void loadUserInfo(String userId) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    updateUI(user);

                    // Lưu user vào session & cache
                    userSession.saveUser(user);
                    AppCache.getInstance().setCurrentUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AccountActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEditInfo.setEnabled(true);
        btnEditInfo.setAlpha(1f);

        btnEditInfo.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerUser, new UpdateAccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setText("Đăng xuất");
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /** Hàm dùng lại để cập nhật giao diện */
    private void updateUI(User user) {
        tvFullName.setText(user.getName());
        tvEmail.setText("Email: " + user.getEmail());
        tvPhone.setText("Số điện thoại: " + user.getPhone());
        tvAddress.setText("Địa chỉ: " + user.getAddress());

        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(AccountActivity.this).load(user.getAvatarUrl()).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_account);
        }
    }

    /**  Làm mới khi cập nhật user */
    public void reloadUserData() {
        User user = userSession.getCurrentUser();
        if (user != null) {
            updateUI(user);
        }
    }

    /** Đăng xuất */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Xoá session & cache
                    userSession.logout();
                    AppCache.getInstance().clear();

                    //  Đăng xuất Firebase
                    auth.signOut();

                    //  Về trang chính
                    Intent intent = new Intent(this, MainUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
