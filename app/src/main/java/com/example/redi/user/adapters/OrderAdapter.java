package com.example.redi.user.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;
import com.example.redi.common.utils.Constants;
import com.example.redi.data.repository.OrderRepository;
import com.example.redi.user.fragments.OrderDetailFragment;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> orders;
    private final String state;
    private final FragmentManager fm;
    private final OrderRepository repo = new OrderRepository();

    public OrderAdapter(List<Order> orders, String state, FragmentManager fm) {
        this.orders = orders;
        this.state = state;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Order o = orders.get(pos);

        h.tvOrderId.setText("Mã đơn: " + o.getOrderId());
        h.tvDate.setText("Ngày đặt: " + o.getDateOrder());
        h.tvStatus.setText(o.getStatus());
        h.tvTotal.setText("Tổng: " + NumberFormat.getInstance().format(o.getPayment().getAmount()) + "₫");

        // 🔹 Hiển thị ảnh sản phẩm đầu tiên
        if (o.getBooklist() != null && !o.getBooklist().isEmpty()) {
            CartItem first = o.getBooklist().values().iterator().next();
            Glide.with(h.itemView.getContext())
                    .load(first.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(h.ivImage);
        } else {
            h.ivImage.setImageResource(R.drawable.ic_book_placeholder);
        }

        // 🔹 Nút xem chi tiết đơn hàng
        h.btnDetail.setOnClickListener(v -> {
            // Hiển thị container để chứa fragment chi tiết
            View root = v.getRootView();
            View container = root.findViewById(R.id.containerUser);
            if (container != null) container.setVisibility(View.VISIBLE);

            fm.beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                    )
                    .replace(R.id.containerUser, OrderDetailFragment.newInstance(o))
                    .addToBackStack(null)
                    .commit();
        });

        // 🔹 Nút huỷ đơn hàng
        if (Constants.STATUS_PENDING.equals(o.getStatus())) {
            h.btnCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setOnClickListener(v -> showCancelDialog(v, o));
        } else {
            h.btnCancel.setVisibility(View.GONE);
        }
    }

    /** Hiển thị hộp thoại xác nhận huỷ đơn */
    private void showCancelDialog(View v, Order order) {
        final EditText input = new EditText(v.getContext());
        input.setHint("Nhập lý do huỷ...");

        new AlertDialog.Builder(v.getContext())
                .setTitle("Huỷ đơn hàng")
                .setMessage("Nhập lý do huỷ đơn hàng này:")
                .setView(input)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(v.getContext(), "Vui lòng nhập lý do!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    order.setStatus(Constants.STATUS_CANCELLED);
                    if (order.getPayment() != null)
                        order.getPayment().setStatus(Constants.PAYMENT_FAILED);
                    order.setCancelReason(reason);
                    repo.updateOrder(order);
                    Toast.makeText(v.getContext(), "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvOrderId, tvDate, tvStatus, tvTotal;
        Button btnDetail, btnCancel;

        ViewHolder(View v) {
            super(v);
            ivImage = v.findViewById(R.id.ivOrderImage);
            tvOrderId = v.findViewById(R.id.tvOrderId);
            tvDate = v.findViewById(R.id.tvDateOrder);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvTotal = v.findViewById(R.id.tvTotal);
            btnDetail = v.findViewById(R.id.btnViewDetail);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}
