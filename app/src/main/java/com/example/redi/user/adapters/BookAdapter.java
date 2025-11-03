package com.example.redi.user.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.Book;
import androidx.fragment.app.FragmentActivity;
import com.example.redi.user.fragments.BookDetailFragment;



import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH> {

    List<Book> list;

    public BookAdapter(List<Book> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH holder, int position) {
        Book book = list.get(position);
        holder.title.setText(book.getTitle());
        holder.price.setText(book.getPrice() + " VNĐ");

        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())
                .into(holder.img);

        // ✅ Khi click vào sách -> mở BookDetailFragment
        holder.itemView.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) v.getContext();
            BookDetailFragment fragment = BookDetailFragment.newInstance(book);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerUser, fragment)
                    .addToBackStack(null) // để quay lại được Home
                    .commit();
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class BookVH extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView img;

        public BookVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            price = itemView.findViewById(R.id.tvPrice);
            img = itemView.findViewById(R.id.ivBook);
        }
    }
    public void updateList(List<Book> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

}
