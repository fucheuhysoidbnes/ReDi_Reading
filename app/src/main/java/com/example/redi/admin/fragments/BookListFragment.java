package com.example.redi.admin.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.example.redi.R;
import com.example.redi.admin.activities.ManageBookActivity;
import com.example.redi.admin.adapters.AdminBookAdapter;
import com.example.redi.admin.viewmodel.AdminBookViewModel;
import com.example.redi.common.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BookListFragment extends Fragment {

    private AdminBookViewModel viewModel;
    private AdminBookAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_book_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(AdminBookViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerBooks);
        SearchView searchView = view.findViewById(R.id.searchBooks);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddBook);

        // Adapter
        adapter = new AdminBookAdapter(new AdminBookAdapter.OnBookClick() {
            @Override
            public void onClick(Book book) {
                ((ManageBookActivity) requireActivity())
                        .openFragment(EditBookFragment.newInstance(book), true);
            }

            @Override
            public void onDelete(Book book) {
                confirmDelete(book);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load sách
        viewModel.loadBooks();
        viewModel.bookList.observe(getViewLifecycleOwner(), books -> adapter.setBooks(books));

        // Tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override public boolean onQueryTextChange(String q) {
                adapter.filter(q);
                return true;
            }
        });

        // Thêm mới
        fabAdd.setOnClickListener(v ->
                ((ManageBookActivity) requireActivity())
                        .openFragment(new EditBookFragment(), true)
        );

        // Vuốt xoá
        ItemTouchHelper.SimpleCallback swipeDelete = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
                Book b = adapter.getBookAt(holder.getAdapterPosition());
                confirmDelete(b);
            }
        };

        new ItemTouchHelper(swipeDelete).attachToRecyclerView(recyclerView);
    }

    private void confirmDelete(Book book) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá sách")
                .setMessage("Bạn có chắc muốn xoá \"" + book.getTitle() + "\" ?")
                .setPositiveButton("Xoá", (d, w) -> viewModel.deleteBook(book.getBook_id()))
                .setNegativeButton("Hủy", null)
                .show();
    }
}
