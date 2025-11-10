package com.example.redi.user.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;
import com.example.redi.data.repository.CartRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentFragment extends Fragment {
    private ImageView imgQr, btnBack;
    private TextView tvTimer, tvAmount;
    private Button btnCancel, btnConfirm;
    private ImageButton btnMB, btnVCB, btnBIDV, btnZaloPay;

    private Order order;
    private final OrderRepository orderRepo = new OrderRepository();
    private final CartRepository cartRepo = new CartRepository();
    private CountDownTimer timer;

    public static PaymentFragment newInstance(Order order) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle b = new Bundle();
        b.putSerializable("order", order);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_payment, container, false);

        btnBack = v.findViewById(R.id.btnBack);
        imgQr = v.findViewById(R.id.imgQr);
        tvTimer = v.findViewById(R.id.tvTimer);
        tvAmount = v.findViewById(R.id.tvAmount);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnConfirm = v.findViewById(R.id.btnConfirm);
        btnMB = v.findViewById(R.id.btnMB);
        btnVCB = v.findViewById(R.id.btnVCB);
        btnBIDV = v.findViewById(R.id.btnBIDV);
        btnZaloPay = v.findViewById(R.id.btnZaloPay);

        if (getArguments() != null)
            order = (Order) getArguments().getSerializable("order");

        if (order != null && order.getPayment() != null) {
            Glide.with(this).load(order.getPayment().getQrImageUrl()).into(imgQr);
            tvAmount.setText("Số tiền: " + order.getPayment().getAmount() + "₫");
            startTimer();
        }

        btnBack.setOnClickListener(v1 -> requireActivity().getSupportFragmentManager().popBackStack());
        btnCancel.setOnClickListener(v1 -> {
            Toast.makeText(requireContext(), "Đã huỷ thanh toán", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnConfirm.setOnClickListener(v1 -> {
            order.getPayment().setStatus("paid");
            orderRepo.createOrder(order, new DataSourceCallback<Void>() {
                @Override
                public void onSuccess(Void r) {
                    Toast.makeText(requireContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                    removeFromCart(order, () -> {
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containerUser, new CartFragment(), "CartFragment")
                                .commitAllowingStateLoss();
                    });
                }

                @Override
                public void onError(String e) {
                    Toast.makeText(requireContext(), "Lỗi: " + e, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnMB.setOnClickListener(v1 -> openBankApp("mbbank://"));
        btnVCB.setOnClickListener(v1 -> openBankApp("vcbpay://"));
        btnBIDV.setOnClickListener(v1 -> openBankApp("bidvsmartbanking://"));
        btnZaloPay.setOnClickListener(v1 -> openBankApp("zalopay://"));

        return v;
    }

    private void openBankApp(String uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ứng dụng chưa được cài đặt", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromCart(Order order, Runnable onComplete) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        cartRepo.findCartByUser(uid, new DataSourceCallback<com.example.redi.common.models.Cart>() {
            @Override
            public void onSuccess(com.example.redi.common.models.Cart cart) {
                if (cart == null) {
                    if (onComplete != null) onComplete.run();
                    return;
                }

                final int[] pending = {order.getBooklist().size()};
                for (String bookId : order.getBooklist().keySet()) {
                    FirebaseDatabase.getInstance()
                            .getReference("carts")
                            .child(cart.getCartId())
                            .child("booklist")
                            .child(bookId)
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                pending[0]--;
                                if (pending[0] <= 0 && onComplete != null) onComplete.run();
                            });
                }
            }

            @Override
            public void onError(String error) {
                if (onComplete != null) onComplete.run();
            }
        });
    }

    private void startTimer() {
        long remain = order.getPayment().getExpiredAt() - System.currentTimeMillis();
        timer = new CountDownTimer(remain, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Thời gian còn lại: " + (millisUntilFinished / 1000) + "s");
            }
            @Override
            public void onFinish() {
                tvTimer.setText("QR đã hết hạn!");
                btnConfirm.setEnabled(false);
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) timer.cancel();
    }
}
