package com.example.redi.admin.fragments;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.redi.R;
import com.example.redi.admin.adapters.AdminOrderBookAdapter;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;
import com.example.redi.common.utils.Constants;

import java.util.*;

public class OrderDetailFragment extends Fragment {

    private static final String ARG_ORDER_ID = "order_id";
    private String orderId;
    private OrderRepository repository;
    private TextView tvOrderId, tvUser, tvAddress, tvPhone, tvStatus, tvPayment, tvDateOrder, tvDateReceive, tvCancelReason, tvCancelAt, tvTotal;
    private Button btnChangeStatus, btnMarkPaid, btnCancelOrder;
    private RecyclerView rvBooks;

    public static OrderDetailFragment newInstance(String orderId) {
        OrderDetailFragment f = new OrderDetailFragment();
        Bundle b = new Bundle();
        b.putString(ARG_ORDER_ID, orderId);
        f.setArguments(b);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_order_detail, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new OrderRepository();
        if (getArguments() != null) orderId = getArguments().getString(ARG_ORDER_ID);

        tvOrderId = v.findViewById(R.id.tvDetailOrderId);
        tvUser = v.findViewById(R.id.tvDetailUser);
        tvAddress = v.findViewById(R.id.tvDetailAddress);
        tvPhone = v.findViewById(R.id.tvDetailPhone);
        tvStatus = v.findViewById(R.id.tvDetailStatus);
        tvPayment = v.findViewById(R.id.tvDetailPayment);
        tvDateOrder = v.findViewById(R.id.tvDetailDateOrder);
        tvDateReceive = v.findViewById(R.id.tvDetailDateReceive);
        tvCancelReason = v.findViewById(R.id.tvCancelReason);
        tvCancelAt = v.findViewById(R.id.tvCancelAt);
        tvTotal = v.findViewById(R.id.tvDetailTotal);
        btnChangeStatus = v.findViewById(R.id.btnChangeStatus);
        btnMarkPaid = v.findViewById(R.id.btnMarkPaid);
        btnCancelOrder = v.findViewById(R.id.btnCancelOrder);
        rvBooks = v.findViewById(R.id.rvBookList);

        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        loadOrder();

        btnChangeStatus.setOnClickListener(x -> showChangeStatusFlow());
        btnMarkPaid.setOnClickListener(x -> markPaymentPaid());
        btnCancelOrder.setOnClickListener(x -> showCancelDialog());
    }

    private void loadOrder() {
        repository.getOrderById(orderId, new DataSourceCallback<Order>() {
            @Override public void onSuccess(Order result) {
                if (result == null) return;
                tvOrderId.setText(result.getOrderId());
                tvUser.setText("UserId: " + result.getUserId());
                tvAddress.setText("Địa chỉ: " + safe(result.getAddress()));
                tvPhone.setText("Phone: " + safe(result.getPhone()));
                tvStatus.setText(result.getStatus());
                tvDateOrder.setText("Đặt: " + safe(result.getDateOrder()));
                tvDateReceive.setText("Nhận: " + (result.getDateReceive() == null ? "-" : result.getDateReceive()));
                if (result.getCancelReason() != null && !result.getCancelReason().isEmpty()) {
                    tvCancelReason.setText("Lý do: " + result.getCancelReason());
                    tvCancelAt.setText("Thời gian huỷ: " + (result.getCancelAt() > 0 ? new Date(result.getCancelAt()).toString() : "-"));
                } else {
                    tvCancelReason.setText("");
                    tvCancelAt.setText("");
                }

                if (result.getPayment() != null) {
                    tvPayment.setText("Phương thức: " + result.getPayment().getMethod() + " / " + result.getPayment().getStatus());
                } else tvPayment.setText("N/A");

                // list books
                List<CartItem> items = new ArrayList<>();
                if (result.getBooklist() != null) items.addAll(result.getBooklist().values());
                AdminOrderBookAdapter bookAdapter = new AdminOrderBookAdapter(items);
                rvBooks.setAdapter(bookAdapter);

                // compute total
                long total = 0;
                for (CartItem ci : items) total += (long) ci.getPrice() * ci.getQty();
                tvTotal.setText("Tổng: " + java.text.NumberFormat.getInstance().format(total));

                // Buttons visibility / rules
                applyButtonsByStatus(result.getStatus());
            }
            @Override public void onError(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(String s) { return s == null ? "" : s; }

    private void applyButtonsByStatus(String status) {
        if (Constants.STATUS_CANCELLED.equals(status)) {
            // can't do any action
            btnCancelOrder.setVisibility(View.GONE);
            btnMarkPaid.setVisibility(View.GONE);
            btnChangeStatus.setVisibility(View.GONE);
        } else if (Constants.STATUS_RECEIVED.equals(status)) {
            // already received: only view review (we show elsewhere)
            btnCancelOrder.setVisibility(View.GONE);
            btnMarkPaid.setVisibility(View.GONE);
            btnChangeStatus.setVisibility(View.GONE);
        } else if (Constants.STATUS_DELIVERING.equals(status)) {
            // delivering -> can only go to RECEIVED
            btnCancelOrder.setVisibility(View.GONE); // cannot cancel when delivering (business rule)
            btnMarkPaid.setVisibility(View.VISIBLE);
            btnChangeStatus.setVisibility(View.VISIBLE);
        } else { // pending
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnMarkPaid.setVisibility(View.VISIBLE);
            btnChangeStatus.setVisibility(View.VISIBLE);
        }
    }

    private void showChangeStatusFlow() {
        // Load current order to decide allowed transitions
        repository.getOrderById(orderId, new DataSourceCallback<Order>() {
            @Override public void onSuccess(Order o) {
                if (o == null) return;
                List<String> options = new ArrayList<>();
                if (Constants.STATUS_PENDING.equals(o.getStatus())) {
                    options.add(Constants.STATUS_DELIVERING);
                    options.add(Constants.STATUS_CANCELLED);
                } else if (Constants.STATUS_DELIVERING.equals(o.getStatus())) {
                    options.add(Constants.STATUS_RECEIVED);
                }

                if (options.isEmpty()) {
                    Toast.makeText(getContext(), "Không có trạng thái hợp lệ để chuyển", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] arr = options.toArray(new String[0]);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Chọn trạng thái")
                        .setItems(arr, (dialog, which) -> {
                            String chosen = arr[which];
                            if (Constants.STATUS_DELIVERING.equals(chosen)) {
                                // set dateReceive by date picker
                                pickDateForReceive(date -> {
                                    repository.updateOrderStatusAndDateReceive(orderId, Constants.STATUS_DELIVERING, date);
                                    Toast.makeText(getContext(), "Cập nhật Đang giao", Toast.LENGTH_SHORT).show();
                                    loadOrder();
                                });
                            } else if (Constants.STATUS_RECEIVED.equals(chosen)) {
                                repository.updateOrderStatus(orderId, Constants.STATUS_RECEIVED);
                                Toast.makeText(getContext(), "Cập nhật Đã nhận hàng", Toast.LENGTH_SHORT).show();
                                loadOrder();
                            } else if (Constants.STATUS_CANCELLED.equals(chosen)) {
                                showCancelDialog();
                            }
                        }).show();
            }
            @Override public void onError(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // date picker helper
    private void pickDateForReceive(DatePickedCallback cb) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    // month is 0-based
                    String mm = String.format(Locale.getDefault(), "%02d", month + 1);
                    String dd = String.format(Locale.getDefault(), "%02d", dayOfMonth);
                    String date = String.format(Locale.getDefault(), "%d-%s-%s", year, mm, dd);
                    cb.onPicked(date);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private interface DatePickedCallback { void onPicked(String date); }

    private void markPaymentPaid() {
        repository.updatePaymentStatus(orderId, com.example.redi.common.utils.Constants.PAYMENT_PAID);
        Toast.makeText(getContext(), "Đánh dấu đã thanh toán", Toast.LENGTH_SHORT).show();
        loadOrder();
    }

    private void showCancelDialog() {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(requireContext())
                .setTitle("Lý do huỷ")
                .setView(input)
                .setPositiveButton("Huỷ đơn", (d, w) -> {
                    String reason = input.getText().toString();
                    repository.cancelOrderWithTimestamp(orderId, reason);
                    Toast.makeText(getContext(), "Đã huỷ đơn", Toast.LENGTH_SHORT).show();
                    loadOrder();
                })
                .setNegativeButton("Thoát", null)
                .show();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        repository.removeListeners();
    }
}
