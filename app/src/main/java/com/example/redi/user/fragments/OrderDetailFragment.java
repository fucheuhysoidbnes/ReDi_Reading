package com.example.redi.user.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;
import com.example.redi.common.utils.Constants;
import com.example.redi.data.repository.OrderRepository;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER = "order_data";

    private TextView tvOrderId, tvStatus, tvTotal, tvPaymentMethod, tvAddress, tvPhone, tvDateOrder;
    private ImageView ivQr;
    private Button btnCancel, btnReview;
    private RecyclerView recyclerBooks;

    private Order currentOrder;
    private OrderRepository repo;
    private BookInOrderAdapter adapter;

    public static OrderDetailFragment newInstance(Order order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.client_fragment_order_detail, container, false);

        // Ánh xạ view
        tvOrderId = v.findViewById(R.id.tvOrderId);
        tvStatus = v.findViewById(R.id.tvStatus);
        tvTotal = v.findViewById(R.id.tvTotal);
        tvPaymentMethod = v.findViewById(R.id.tvPaymentMethod);
        tvAddress = v.findViewById(R.id.tvAddress);
        tvPhone = v.findViewById(R.id.tvPhone);
        tvDateOrder = v.findViewById(R.id.tvDateOrder);
        ivQr = v.findViewById(R.id.ivQrCode);
        btnCancel = v.findViewById(R.id.btnCancelOrder);
        btnReview = v.findViewById(R.id.btnReviewOrder);
        recyclerBooks = v.findViewById(R.id.recyclerBooks);

        recyclerBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        repo = new OrderRepository();

        if (getArguments() != null) {
            currentOrder = (Order) getArguments().getSerializable(ARG_ORDER);
            bindOrderData(currentOrder);
            ImageView btnBack = v.findViewById(R.id.btnBack);
            btnBack.setOnClickListener(view -> {
                requireActivity().getSupportFragmentManager().popBackStack();
                requireActivity().findViewById(R.id.containerUser).setVisibility(View.GONE);
            });

        }

        return v;
    }

    /** Hiển thị dữ liệu chi tiết đơn hàng */
    private void bindOrderData(Order o) {
        if (o == null) return;

        tvOrderId.setText("Mã đơn: " + o.getOrderId());
        tvStatus.setText("Trạng thái: " + o.getStatus());
        tvTotal.setText("Tổng tiền: " +
                NumberFormat.getInstance().format(o.getPayment().getAmount()) + "₫");
        tvPaymentMethod.setText("Phương thức thanh toán: " + o.getPayment().getMethod());
        tvAddress.setText("Địa chỉ: " + o.getAddress());
        tvPhone.setText("SĐT: " + o.getPhone());
        tvDateOrder.setText("Ngày đặt: " + o.getDateOrder());

        // QR thanh toán
        if (o.getPayment() != null && !TextUtils.isEmpty(o.getPayment().getQrImageUrl())) {
            ivQr.setVisibility(View.VISIBLE);
            Glide.with(this).load(o.getPayment().getQrImageUrl()).into(ivQr);
        } else {
            ivQr.setVisibility(View.GONE);
        }

        // Danh sách sách trong đơn
        List<CartItem> items = new ArrayList<>();
        if (o.getBooklist() != null) {
            for (Map.Entry<String, CartItem> entry : o.getBooklist().entrySet()) {
                items.add(entry.getValue());
            }
        }

        adapter = new BookInOrderAdapter(items);
        recyclerBooks.setAdapter(adapter);

        // Xử lý hiển thị nút huỷ hoặc đánh giá
        if (Constants.STATUS_PENDING.equals(o.getStatus())) {
            btnCancel.setVisibility(View.VISIBLE);
            btnReview.setVisibility(View.GONE);
            btnCancel.setOnClickListener(v -> showCancelDialog());
        } else if (Constants.STATUS_RECEIVED.equals(o.getStatus())) {
            btnCancel.setVisibility(View.GONE);
            btnReview.setVisibility(View.VISIBLE);
            btnReview.setOnClickListener(v -> showReviewDialog());
        } else {
            btnCancel.setVisibility(View.GONE);
            btnReview.setVisibility(View.GONE);
        }
    }

    /** Dialog huỷ đơn hàng */
    private void showCancelDialog() {
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập lý do huỷ...");

        new AlertDialog.Builder(requireContext())
                .setTitle("Huỷ đơn hàng")
                .setMessage("Bạn có chắc chắn muốn huỷ đơn hàng này?")
                .setView(input)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Vui lòng nhập lý do!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentOrder.setStatus(Constants.STATUS_CANCELLED);
                    if (currentOrder.getPayment() != null)
                        currentOrder.getPayment().setStatus(Constants.PAYMENT_FAILED);
                    currentOrder.setCancelReason(reason);
                    repo.updateOrder(currentOrder);
                    Toast.makeText(requireContext(), "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /** Dialog đánh giá đơn hàng */
    private void showReviewDialog() {
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập đánh giá của bạn...");

        new AlertDialog.Builder(requireContext())
                .setTitle("Đánh giá đơn hàng")
                .setView(input)
                .setPositiveButton("Gửi", (dialog, which) -> {
                    String review = input.getText().toString().trim();
                    if (review.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Vui lòng nhập đánh giá!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentOrder.setReview(review);
                    repo.updateOrder(currentOrder);
                    Toast.makeText(requireContext(), "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    /** Adapter hiển thị sách trong đơn */
    static class BookInOrderAdapter extends RecyclerView.Adapter<BookInOrderAdapter.ViewHolder> {
        private final List<CartItem> items;

        BookInOrderAdapter(List<CartItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_book_in_order, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItem item = items.get(position);
            holder.tvTitle.setText(item.getTitle());
            holder.tvQty.setText("Số lượng: " + item.getQty());
            holder.tvPrice.setText(NumberFormat.getInstance().format(item.getPrice()) + "₫");

            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvTitle, tvQty, tvPrice;

            ViewHolder(View v) {
                super(v);
                ivImage = v.findViewById(R.id.ivBook);
                tvTitle = v.findViewById(R.id.tvBookTitle);
                tvQty = v.findViewById(R.id.tvBookQty);
                tvPrice = v.findViewById(R.id.tvBookPrice);
            }
        }
    }
}
