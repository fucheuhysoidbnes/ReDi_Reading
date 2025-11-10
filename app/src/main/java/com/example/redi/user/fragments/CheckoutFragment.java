package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redi.R;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;
import com.example.redi.common.models.Payment;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.CartRepository;
import com.example.redi.data.repository.OrderRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckoutFragment extends Fragment {

    private ImageView btnBack;
    private EditText etAddress, etPhone;
    private RadioGroup rgPayment;
    private Button btnConfirm;
    private int totalAmount;
    private Map<String, CartItem> booklist;

    private final OrderRepository orderRepo = new OrderRepository();
    private final CartRepository cartRepo = new CartRepository();

    public static CheckoutFragment newInstance(int total, Map<String, CartItem> items) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle b = new Bundle();
        b.putInt("totalAmount", total);
        b.putSerializable("booklist", (HashMap) items);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_checkout, container, false);

        btnBack = v.findViewById(R.id.btnBack);
        etAddress = v.findViewById(R.id.etAddress);
        etPhone = v.findViewById(R.id.etPhone);
        rgPayment = v.findViewById(R.id.rgPayment);
        btnConfirm = v.findViewById(R.id.btnConfirm);

        if (getArguments() != null) {
            totalAmount = getArguments().getInt("totalAmount");
            booklist = (Map<String, CartItem>) getArguments().getSerializable("booklist");
        }

        btnBack.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
        btnConfirm.setOnClickListener(view -> handleCheckout());

        return v;
    }

    private void handleCheckout() {
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if (address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgPayment.getCheckedRadioButtonId();
        String paymentMethod = checkedId == R.id.rbBank ? "bank_transfer" : "cod";

        Order order = new Order();
        order.setUserId(FirebaseAuth.getInstance().getUid());
        order.setAddress(address);
        order.setPhone(phone);
        order.setBooklist(booklist);
        order.setStatus("Chờ xác nhận");

        String dateNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        order.setDateOrder(dateNow);

        Payment payment = new Payment();
        payment.setMethod(paymentMethod);
        payment.setStatus("pending");
        payment.setAmount(totalAmount);

        if (paymentMethod.equals("bank_transfer")) {
            long expiredAt = System.currentTimeMillis() + 60_000;
            String qrUrl = "https://img.vietqr.io/image/VCB-0123456789-compact.png?amount=" + totalAmount + "&addInfo=ThanhToanSach";
            payment.setBankName("Vietcombank");
            payment.setQrImageUrl(qrUrl);
            payment.setExpiredAt(expiredAt);
        }

        order.setPayment(payment);

        // Chuyển tiếp đến fragment hiển thị QR nếu chọn online
        if (paymentMethod.equals("bank_transfer")) {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerUser, PaymentFragment.newInstance(order))
                    .addToBackStack(null)
                    .commit();
        } else {
            createOrder(order);
        }
    }

    private void createOrder(Order order) {
        orderRepo.createOrder(order, new DataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(requireContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                removeFromCart(order.getBooklist());
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 🧹 Xóa sản phẩm khỏi giỏ hàng khi đặt xong */
    private void removeFromCart(Map<String, CartItem> booklist) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        cartRepo.findCartByUser(uid, new DataSourceCallback<com.example.redi.common.models.Cart>() {
            @Override
            public void onSuccess(com.example.redi.common.models.Cart cart) {
                if (cart == null) return;

                for (String bookId : booklist.keySet()) {
                    FirebaseDatabase.getInstance()
                            .getReference("carts")
                            .child(cart.getCartId())
                            .child("booklist")
                            .child(bookId)
                            .removeValue();
                }
            }

            @Override
            public void onError(String error) { }
        });
    }
}
