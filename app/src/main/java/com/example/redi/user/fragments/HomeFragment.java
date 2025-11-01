package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.redi.R;
import com.example.redi.common.models.Book;
import com.example.redi.common.utils.AppCache;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.user.adapters.BookAdapter;
import com.example.redi.user.viewmodel.BookViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rv;
    TextView tvUser;
    BookViewModel viewModel;
    BookAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_home, container, false);

        rv = v.findViewById(R.id.rvBooks);
        tvUser = v.findViewById(R.id.tvUser);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new BookViewModel();

        loadUser();
        showCachedBooks();   // ✅ hiển thị cache trước
        loadBooks();         // ✅ rồi cập nhật dữ liệu mới từ Firebase

        return v;
    }

    private void loadUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) tvUser.setText("Xin chào, " + user.getEmail());
        else tvUser.setText("Chưa đăng nhập");
    }

    private void showCachedBooks() {
        List<Book> cached = AppCache.getInstance().getBooks();
        if (cached != null && !cached.isEmpty()) {
            adapter = new BookAdapter(cached);
            rv.setAdapter(adapter);
        }
    }

    private void loadBooks() {
        viewModel.loadBooks(new DataSourceCallback<List<Book>>() {
            @Override
            public void onSuccess(List<Book> data) {
                if (adapter == null) {
                    adapter = new BookAdapter(data);
                    rv.setAdapter(adapter);
                } else {
                    adapter = new BookAdapter(data);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
