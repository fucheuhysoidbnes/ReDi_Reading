package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.common.models.Cart;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.utils.UserSession;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.CartRepository;
import com.example.redi.user.adapters.CartAdapter;

import java.util.*;

public class CartFragment extends Fragment implements CartAdapter.CartActionListener {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice, tvLoginNotice;
    private ProgressBar progressBar;
    private Button btnCheckout, btnDelete;
    private LinearLayout layoutBottom; // phần chứa tổng tiền + nút

    private CartRepository cartRepository;
    private Cart currentCart;
    private final List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private int totalPrice = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View v = inflater.inflate(R.layout.client_fragment_cart, container, false);

        // Ánh xạ view
        recyclerView = v.findViewById(R.id.recyclerCart);
        tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        progressBar = v.findViewById(R.id.progressBar);
        btnCheckout = v.findViewById(R.id.btnCheckout);
        btnDelete = v.findViewById(R.id.btnDelete);
        layoutBottom = v.findViewById(R.id.layoutBottom);

        // Thêm TextView thông báo đăng nhập (tạo động, vì XML cũ chưa có)
        tvLoginNotice = new TextView(requireContext());
        tvLoginNotice.setText("Bạn cần đăng nhập để xem giỏ hàng");
        tvLoginNotice.setTextColor(getResources().getColor(R.color.brown));
        tvLoginNotice.setTextSize(16);
        tvLoginNotice.setGravity(Gravity.CENTER);
        tvLoginNotice.setVisibility(View.GONE);

        // Thêm vào root layout
        ((ViewGroup) v).addView(tvLoginNotice,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartRepository = new CartRepository();
        adapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        btnCheckout.setOnClickListener(v1 -> proceedToCheckout());
        btnDelete.setOnClickListener(v1 -> deleteSelectedItems());

        // Lắng nghe khi Checkout/PaymentFragment báo giỏ hàng đã cập nhật
        getParentFragmentManager().setFragmentResultListener("cart_update", this, (requestKey, bundle) -> {
            boolean updated = bundle.getBoolean("cart_updated", false);
            if (updated) {
                Toast.makeText(requireContext(), "Giỏ hàng đã được cập nhật sau khi đặt hàng", Toast.LENGTH_SHORT).show();
                loadCartData();
            }
        });

        loadCartData();
        return v;
    }

    private void loadCartData() {
        progressBar.setVisibility(View.VISIBLE);
        UserSession userSession = new UserSession(requireContext());

        if (!userSession.isLoggedIn() || userSession.getCurrentUser() == null) {
            // Khi chưa đăng nhập
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            tvLoginNotice.setVisibility(View.VISIBLE);
            return;
        }

        // Nếu đã đăng nhập
        recyclerView.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.VISIBLE);
        tvLoginNotice.setVisibility(View.GONE);

        String userId = userSession.getCurrentUser().getId();
        if (userId == null || userId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            tvTotalPrice.setText("Không thể tải giỏ hàng");
            return;
        }

        cartRepository.findCartByUser(userId, new DataSourceCallback<Cart>() {
            @Override
            public void onSuccess(Cart result) {
                progressBar.setVisibility(View.GONE);

                cartItems.clear();
                if (result == null || result.getBooklist() == null || result.getBooklist().isEmpty()) {
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                    return;
                }

                currentCart = result;
                for (Map.Entry<String, CartItem> entry : result.getBooklist().entrySet()) {
                    cartItems.add(entry.getValue());
                }

                adapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi tải giỏ hàng: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        totalPrice = 0;
        for (CartItem item : adapter.getSelectedItems()) {
            totalPrice += item.getPrice() * item.getQty();
        }
        tvTotalPrice.setText("Tổng tiền: " + totalPrice + "₫");
    }

    private void proceedToCheckout() {
        List<CartItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), "Hãy chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, CartItem> selectedMap = new HashMap<>();
        int total = 0;
        for (CartItem item : selected) {
            selectedMap.put(item.getBook_id(), item);
            total += item.getPrice() * item.getQty();
        }

        CheckoutFragment checkoutFragment = CheckoutFragment.newInstance(total, selectedMap);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerUser, checkoutFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteSelectedItems() {
        if (currentCart == null) return;
        List<CartItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), "Chọn sản phẩm để xoá", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CartItem item : selected) {
            com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("carts")
                    .child(currentCart.getCartId())
                    .child("booklist")
                    .child(item.getBook_id())
                    .removeValue();
        }

        cartItems.removeAll(selected);
        adapter.notifyDataSetChanged();
        updateTotalPrice();
        Toast.makeText(requireContext(), "Đã xoá sản phẩm được chọn", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemCheckedChanged(List<CartItem> selectedItems) {
        updateTotalPrice();
    }

    @Override
    public void onQuantityChanged(CartItem item) {
        if (currentCart == null) return;

        cartRepository.addOrUpdateItem(currentCart.getCartId(), item, new DataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateTotalPrice();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Cập nhật thất bại: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
    }
}
