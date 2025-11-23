package com.example.redi.admin.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redi.R;
import com.example.redi.common.models.User;
import com.example.redi.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddEditUserFragment extends Fragment {

    private static final String ARG_USER = "arg_user";

    private User editingUser;
    private EditText etName, etEmail, etPhone, etAddress, etPassword;
    private Spinner spRole;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private UserRepository repo;

    public static AddEditUserFragment newInstance(User user) {
        AddEditUserFragment f = new AddEditUserFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_edit_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        repo = new UserRepository();

        etName = v.findViewById(R.id.et_name);
        etEmail = v.findViewById(R.id.et_email);
        etPassword = v.findViewById(R.id.et_password);
        etPhone = v.findViewById(R.id.et_phone);
        etAddress = v.findViewById(R.id.et_address);
        spRole = v.findViewById(R.id.sp_role);
        btnSave = v.findViewById(R.id.btn_save);
        btnCancel = v.findViewById(R.id.btn_delete); // rename: DELETE → CANCEL
        progressBar = v.findViewById(R.id.progress_bar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        if (getArguments() != null) {
            editingUser = (User) getArguments().getSerializable(ARG_USER);
        }

        if (editingUser != null) {
            // EDIT USER
            etName.setText(editingUser.getName());
            etEmail.setText(editingUser.getEmail());
            etEmail.setEnabled(false);

            etPassword.setVisibility(View.GONE);

            etPhone.setText(editingUser.getPhone());
            etAddress.setText(editingUser.getAddress());

            int pos = adapter.getPosition(editingUser.getRole());
            if (pos >= 0) spRole.setSelection(pos);

            btnCancel.setText("Hủy"); // không phải xoá
            btnCancel.setVisibility(View.VISIBLE);

        } else {
            // CREATE USER
            btnCancel.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(view -> saveUser());
        btnCancel.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String role = spRole.getSelectedItem().toString();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Tên và email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // ─────────────────────────────────────────────────
        // CREATE USER
        // ─────────────────────────────────────────────────
        if (editingUser == null) {

            if (TextUtils.isEmpty(pass)) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {

                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(),
                                    "Auth Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        FirebaseUser fbUser = task.getResult().getUser();
                        if (fbUser == null) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Không tạo được user", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = fbUser.getUid();

                        User newUser = new User(uid, name, email, phone, address, role, "");

                        repo.saveUser(newUser, (error, ref) -> {
                            progressBar.setVisibility(View.GONE);
                            if (error == null) {
                                Toast.makeText(requireContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(requireContext(), "DB Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
            return;
        }

        // ─────────────────────────────────────────────────
        // UPDATE USER
        // ─────────────────────────────────────────────────
        editingUser.setName(name);
        editingUser.setPhone(phone);
        editingUser.setAddress(address);
        editingUser.setRole(role);

        repo.saveUser(editingUser, (error, ref) -> {
            progressBar.setVisibility(View.GONE);
            if (error == null) {
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(requireContext(), "Lỗi DB: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
