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

public class DashboardTopProductAdapter extends RecyclerView.Adapter<DashboardTopProductAdapter.VH> {

    private final List<CartItem> list;

    public DashboardTopProductAdapter(List<CartItem> list) {
        this.list = list;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_top_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem c = list.get(position);
        h.tvTitle.setText(c.getTitle());
        h.tvQty.setText("Đã bán: " + c.getQty());
        h.tvPrice.setText(NumberFormat.getInstance().format(c.getPrice()) + "đ");

        Glide.with(h.img.getContext())
                .load(c.getImageUrl())
                .placeholder(R.drawable.ic_books)
                .into(h.img);
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvQty, tvPrice;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgBook);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvQty = v.findViewById(R.id.tvQty);
            tvPrice = v.findViewById(R.id.tvPrice);
        }
    }
}
