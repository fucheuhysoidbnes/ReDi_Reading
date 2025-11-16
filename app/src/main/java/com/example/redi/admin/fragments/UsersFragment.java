package com.example.redi.admin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.admin.adapters.AdminUserAdapter;
import com.example.redi.admin.activities.ManageAccountActivity;
import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.UserRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UsersFragment extends Fragment implements AdminUserAdapter.OnUserActionListener {

    private RecyclerView rv;
    private AdminUserAdapter adapter;
    private UserRepository repo;
    private ProgressBar progressBar;
    private EditText etSearch;
    private ImageButton btnSearch, btnClear;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        repo = new UserRepository();

        rv = v.findViewById(R.id.rv_users);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminUserAdapter(this);
        rv.setAdapter(adapter);

        progressBar = v.findViewById(R.id.progress_bar);
        etSearch = v.findViewById(R.id.et_search);
        btnSearch = v.findViewById(R.id.btn_search);
        btnClear = v.findViewById(R.id.btn_clear);
        fabAdd = v.findViewById(R.id.fab_add_user);

        // --- 1) Search realtime ---
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim().toLowerCase();
                if (q.isEmpty()) {
                    loadUsers();
                } else {
                    doSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 2) Clear button
        btnClear.setOnClickListener(view -> {
            etSearch.setText("");
            loadUsers();
        });

        // 3) Add user
        fabAdd.setOnClickListener(view -> {
            AddEditUserFragment f = AddEditUserFragment.newInstance(null);
            ((ManageAccountActivity) requireActivity()).openFragment(f, true);
        });

        loadUsers();
    }


    private void doSearch() {
        String q = etSearch.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(q)) {
            loadUsers();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        repo.searchUsersByEmail(q, new DataSourceCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                progressBar.setVisibility(View.GONE);
                adapter.setUsers(result);
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        repo.getAllUsers(new DataSourceCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                progressBar.setVisibility(View.GONE);
                adapter.setUsers(result);
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(User user) {
        AddEditUserFragment f = AddEditUserFragment.newInstance(user);
        ((ManageAccountActivity) requireActivity()).openFragment(f, true);
    }

    @Override
    public void onDelete(User user) {
        AddEditUserFragment f = AddEditUserFragment.newInstance(user);
        ((ManageAccountActivity) requireActivity()).openFragment(f, true);
    }
}
