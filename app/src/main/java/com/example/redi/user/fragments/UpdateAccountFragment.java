package com.example.redi.user.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redi.R;
import com.example.redi.common.models.User;
import com.example.redi.user.activities.AccountActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class UpdateAccountFragment extends Fragment {

    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnSave, btnCancel;

    private DatabaseReference userRef;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_account_update, container, false);

        // Khởi tạo view
        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etPhone = v.findViewById(R.id.etPhone);
        etAddress = v.findViewById(R.id.etAddress);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        if (auth.getCurrentUser() != null) {
            loadUserInfo(auth.getCurrentUser().getUid());
        }

        // Nút lưu thay đổi
        btnSave.setOnClickListener(v1 -> confirmSave());

        // Nút hủy (quay lại)
        btnCancel.setOnClickListener(v2 -> requireActivity().onBackPressed());

        return v;
    }

    private void loadUserInfo(String userId) {
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    etName.setText(user.getName());
                    etEmail.setText(user.getEmail());
                    etPhone.setText(user.getPhone());
                    etAddress.setText(user.getAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải thông tin: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ⚙️ Hiển thị dialog xác nhận trước khi lưu
    private void confirmSave() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận cập nhật")
                .setMessage("Bạn có chắc chắn muốn lưu thay đổi thông tin không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> saveUserInfo())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void saveUserInfo() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Tên và email không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Tạo object User mới với dữ liệu mới
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);
        updatedUser.setAddress(address);
        updatedUser.setAvatarUrl(""); // giữ nguyên rỗng, sau có thể cập nhật sau
        updatedUser.setRole("user");  // hoặc lấy role cũ nếu có

        // Ghi đè toàn bộ node user hiện tại bằng object này
        userRef.child(userId)
                .setValue(updatedUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi tín hiệu về Activity cha để reload
                    if (getActivity() instanceof AccountActivity) {
                        ((AccountActivity) getActivity()).reloadUserData();
                    }

                    // Quay lại
                    requireActivity().getSupportFragmentManager().popBackStack();
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
