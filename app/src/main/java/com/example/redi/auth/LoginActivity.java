package com.example.redi.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redi.R;
import com.example.redi.common.models.User;
import com.example.redi.common.utils.UserSession;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoRegister;
    FirebaseAuth auth;
    UserRepository userRepo;
    UserSession userSession; // ✅ dùng UserSession để lưu user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        auth = FirebaseAuth.getInstance();
        userRepo = new UserRepository();
        userSession = new UserSession(this);

        btnLogin.setOnClickListener(v -> login());
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser fuser = auth.getCurrentUser();
                        if (fuser != null) {
                            // ✅ Gọi repository lấy thông tin user đầy đủ
                            userRepo.getUserById(fuser.getUid(), new DataSourceCallback<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    if (user == null) {
                                        user = new User();
                                        user.setId(fuser.getUid());
                                        user.setEmail(fuser.getEmail());
                                        user.setRole("user");
                                    }

                                    // ✅ Lưu vào UserSession
                                    userSession.saveUser(user);

                                    Toast.makeText(LoginActivity.this,
                                            "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                    Intent intent;
                                    if ("admin".equals(user.getRole())) {
                                        intent = new Intent(LoginActivity.this,
                                                com.example.redi.admin.activities.MainAdminActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this,
                                                com.example.redi.user.activities.MainUserActivity.class);
                                    }

                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(LoginActivity.this,
                                            "Lỗi đọc dữ liệu user: " + error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
