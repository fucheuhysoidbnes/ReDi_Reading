package com.example.redi.user.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.common.models.Book;
import com.example.redi.common.utils.AppCache;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.user.adapters.BookAdapter;
import com.example.redi.user.viewmodel.BookViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private TextView tvUser;
    private EditText etSearch;
    private BookViewModel viewModel;
    private BookAdapter adapter;
    private final List<Book> allBooks = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_home, container, false);

        rv = v.findViewById(R.id.rvBooks);
        tvUser = v.findViewById(R.id.tvUser);
        etSearch = v.findViewById(R.id.etSearch);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new BookViewModel();

        loadUser();
        showCachedBooks();   // hiển thị cache trước
        loadBooks();         // rồi tải dữ liệu mới từ Firebase
        setupSearch();       // kích hoạt tìm kiếm

        return v;
    }

    // ===================== USER INFO ===================== //
    private void loadUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvUser.setText("Xin chào, " + user.getEmail());
        } else {            
            tvUser.setText("Xin chào");
        }
    }

    // ===================== SHOW CACHED BOOKS ===================== //
    private void showCachedBooks() {
        List<Book> cached = AppCache.getInstance().getBooks();
        if (cached != null && !cached.isEmpty()) {
            allBooks.clear();
            allBooks.addAll(cached);
            adapter = new BookAdapter(new ArrayList<>(allBooks));
            rv.setAdapter(adapter);
        }
    }

    // ===================== LOAD FROM FIREBASE ===================== //
    private void loadBooks() {
        viewModel.loadBooks(new DataSourceCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> data) {
                if (data != null && !data.isEmpty()) {
                    allBooks.clear();
                    allBooks.addAll(data);

                    // Cập nhật cache để lần sau Splash có sẵn dữ liệu
                    AppCache.getInstance().setBooks(data);

                    if (adapter == null) {
                        adapter = new BookAdapter(data);
                        rv.setAdapter(adapter);
                    } else {
                        adapter.updateList(data);
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Lỗi tải sách: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================== SEARCH FEATURE ===================== //
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBooks(String keyword) {
        if (adapter == null) return;

        if (keyword.trim().isEmpty()) {
            adapter.updateList(allBooks);
            return;
        }

        List<Book> filtered = new ArrayList<>();
        for (Book b : allBooks) {
            if (b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(b);
            }
        }

        adapter.updateList(filtered);
    }
}
