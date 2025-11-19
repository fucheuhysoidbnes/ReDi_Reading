package com.example.redi.admin.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.VH> {

    public interface OnActionListener {
        void onViewOrder(Order order);
    }

    private final List<Order> origin;
    private List<Order> list;
    private final OnActionListener listener;

    public AdminOrderAdapter(List<Order> items, OnActionListener l) {
        this.origin = new ArrayList<>(items);
        this.list = new ArrayList<>(items);
        this.listener = l;
    }

    public void updateList(List<Order> items) {
        origin.clear();
        origin.addAll(items);
        this.list = new ArrayList<>(origin);
        notifyDataSetChanged();
    }

    // Search only by userId
    public void filter(String q) {
        if (q == null || q.trim().isEmpty()) {
            list = new ArrayList<>(origin);
        } else {
            String k = q.toLowerCase();
            list = origin.stream().filter(o ->
                    o.getUserId() != null && o.getUserId().toLowerCase().contains(k)
            ).collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = list.get(position);
        holder.tvOrderId.setText(o.getOrderId());
        holder.tvStatus.setText(o.getStatus());
        holder.tvPhone.setText(o.getPhone());
        holder.tvDate.setText(o.getDateOrder());

        // badge color by status
        String status = o.getStatus() == null ? "" : o.getStatus();
        switch (status) {
            case com.example.redi.common.utils.Constants.STATUS_PENDING:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
            case com.example.redi.common.utils.Constants.STATUS_DELIVERING:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_delivering);
                break;
            case com.example.redi.common.utils.Constants.STATUS_RECEIVED:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_received);
                break;
            case com.example.redi.common.utils.Constants.STATUS_CANCELLED:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }

        // compute total and load first image
        long total = 0;
        if (o.getBooklist() != null && !o.getBooklist().isEmpty()) {
            Collection<CartItem> items = o.getBooklist().values();
            for (CartItem ci : items) {
                if (ci != null) total += (long) ci.getPrice() * ci.getQty();
            }
            // load first image
            CartItem first = o.getBooklist().values().iterator().next();
            if (first != null && first.getImageUrl() != null && !first.getImageUrl().isEmpty()) {
                Glide.with(holder.imgBook.getContext())
                        .load(first.getImageUrl())
                        .placeholder(R.drawable.ic_books)
                        .into(holder.imgBook);
            } else {
                holder.imgBook.setImageResource(R.drawable.ic_books);
            }
        } else {
            holder.imgBook.setImageResource(R.drawable.ic_books);
        }

        holder.tvTotal.setText(NumberFormat.getInstance().format(total));

        holder.btnDetail.setOnClickListener(v -> listener.onViewOrder(o));
        holder.itemView.setOnClickListener(v -> listener.onViewOrder(o));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvPhone, tvDate, tvTotal;
        ImageView imgBook;
        ImageButton btnDetail;

        VH(@NonNull View v) {
            super(v);
            tvOrderId = v.findViewById(R.id.tvOrderId);
            tvStatus = v.findViewById(R.id.tvOrderStatus);
            tvPhone = v.findViewById(R.id.tvOrderPhone);
            tvDate = v.findViewById(R.id.tvOrderDate);
            tvTotal = v.findViewById(R.id.tvOrderTotal);
            imgBook = v.findViewById(R.id.imgBook);
            btnDetail = v.findViewById(R.id.btnDetail);
        }
    }
}
