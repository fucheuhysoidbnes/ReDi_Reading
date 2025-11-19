package com.example.redi.admin.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.CartItem;

import java.text.NumberFormat;
import java.util.*;

public class AdminOrderBookAdapter extends RecyclerView.Adapter<AdminOrderBookAdapter.VH> {

    private final List<CartItem> items;

    public AdminOrderBookAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order_book, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem ci = items.get(position);
        holder.tvTitle.setText(ci.getTitle());
        holder.tvQty.setText("x" + ci.getQty());
        holder.tvPrice.setText(NumberFormat.getInstance().format(ci.getPrice()));
        Glide.with(holder.img.getContext()).load(ci.getImageUrl()).placeholder(R.drawable.ic_books).into(holder.img);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvQty, tvPrice;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgBookItem);
            tvTitle = v.findViewById(R.id.tvBookTitle);
            tvQty = v.findViewById(R.id.tvBookQty);
            tvPrice = v.findViewById(R.id.tvBookPrice);
        }
    }
}
