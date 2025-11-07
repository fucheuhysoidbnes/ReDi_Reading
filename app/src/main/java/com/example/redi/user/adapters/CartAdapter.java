package com.example.redi.user.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final CartActionListener listener;
    private final List<CartItem> selectedItems = new ArrayList<>();

    public interface CartActionListener {
        void onItemCheckedChanged(List<CartItem> selectedItems);
        void onQuantityChanged(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, CartActionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(item.getPrice() + "VND");
        holder.tvQty.setText(String.valueOf(item.getQty()));
        holder.cbSelect.setChecked(selectedItems.contains(item));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.imgBook);

        //  Chọn / bỏ chọn sản phẩm
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedItems.contains(item)) selectedItems.add(item);
            } else {
                selectedItems.remove(item);
            }
            listener.onItemCheckedChanged(selectedItems);
        });

        //  Nút cộng
        holder.btnPlus.setOnClickListener(v -> {
            int newQty = item.getQty() + 1;
            item.setQty(newQty);
            holder.tvQty.setText(String.valueOf(newQty)); // cập nhật UI ngay
            listener.onQuantityChanged(item); // cập nhật Firebase
        });

        // Nút trừ
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQty() > 1) {
                int newQty = item.getQty() - 1;
                item.setQty(newQty);
                holder.tvQty.setText(String.valueOf(newQty));
                listener.onQuantityChanged(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getSelectedItems() {
        return selectedItems;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView imgBook;
        TextView tvTitle, tvPrice, tvQty;
        ImageButton btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            imgBook = itemView.findViewById(R.id.imgBook);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
