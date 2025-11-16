package com.example.redi.admin.adapters;

import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.Book;

import java.util.*;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.BookVH> {

    private List<Book> fullList = new ArrayList<>();
    private List<Book> displayList = new ArrayList<>();

    public interface OnBookClick {
        void onClick(Book book);
        void onDelete(Book book);
    }

    private final OnBookClick listener;

    public AdminBookAdapter(OnBookClick listener) {
        this.listener = listener;
    }

    public void setBooks(List<Book> list) {
        fullList = new ArrayList<>(list);
        displayList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public Book getBookAt(int pos) { return displayList.get(pos); }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_book, parent, false);
        return new BookVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH h, int pos) {
        Book b = displayList.get(pos);

        h.title.setText(b.getTitle());
        h.price.setText(b.getPrice() + "đ");

        // Load ảnh
        Glide.with(h.itemView.getContext())
                .load(b.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(h.thumb);

        // Click sửa
        h.itemView.setOnClickListener(v -> listener.onClick(b));

        // Click xoá
        h.btnDelete.setOnClickListener(v -> listener.onDelete(b));
    }

    @Override
    public int getItemCount() { return displayList.size(); }

    public void filter(String q) {
        q = q.toLowerCase().trim();
        displayList.clear();

        if (q.isEmpty()) displayList.addAll(fullList);
        else {
            for (Book b : fullList) {
                if (b.getTitle().toLowerCase().contains(q)) displayList.add(b);
            }
        }
        notifyDataSetChanged();
    }

    static class BookVH extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView thumb, btnDelete;

        public BookVH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.txtTitle);
            price = v.findViewById(R.id.txtPrice);
            thumb = v.findViewById(R.id.imgThumb);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
