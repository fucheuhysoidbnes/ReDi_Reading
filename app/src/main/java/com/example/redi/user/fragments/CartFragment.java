package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment implements CartAdapter.CartActionListener {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private ProgressBar progressBar;
    private Button btnCheckout, btnDelete;

    private CartRepository cartRepository;
    private Cart currentCart;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private int totalPrice = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_cart, container, false);

        recyclerView = v.findViewById(R.id.recyclerCart);
        tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        progressBar = v.findViewById(R.id.progressBar);
        btnCheckout = v.findViewById(R.id.btnCheckout);
        btnDelete = v.findViewById(R.id.btnDelete);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartRepository = new CartRepository();

        adapter = new CartAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        btnCheckout.setOnClickListener(view ->
                Toast.makeText(requireContext(), "Chức năng thanh toán sẽ được thêm sau", Toast.LENGTH_SHORT).show()
        );

        btnDelete.setOnClickListener(view -> deleteSelectedItems());

        loadCartData();
        return v;
    }

    private void loadCartData() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = new UserSession(requireContext()).getCurrentUser().getId();

        cartRepository.findCartByUser(userId, new DataSourceCallback<Cart>() {
            @Override
            public void onSuccess(Cart result) {
                progressBar.setVisibility(View.GONE);

                cartItems.clear();
                if (result == null || result.getBooklist() == null || result.getBooklist().isEmpty()) {
                    Toast.makeText(requireContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
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

    /**  Cập nhật lại giá tổng */
    private void updateTotalPrice() {
        totalPrice = 0;
        for (CartItem item : adapter.getSelectedItems()) {
            totalPrice += item.getPrice() * item.getQty();
        }
        tvTotalPrice.setText("Tổng tiền: " + totalPrice + "VND");
    }

    /** Xoá sản phẩm được chọn */
    private void deleteSelectedItems() {
        if (currentCart == null) return;
        List<CartItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), "Chọn sản phẩm để xoá", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CartItem item : selected) {
            // Xoá từng sản phẩm trong Firebase
            String cartId = currentCart.getCartId();
            String bookId = item.getBook_id();
            com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("carts")
                    .child(cartId)
                    .child("booklist")
                    .child(bookId)
                    .removeValue();
        }

        // Xoá khỏi danh sách hiển thị (cập nhật UI)
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem i = iterator.next();
            for (CartItem s : selected) {
                if (s.getBook_id().equals(i.getBook_id())) {
                    iterator.remove();
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateTotalPrice();

        Toast.makeText(requireContext(), "Đã xoá sản phẩm được chọn", Toast.LENGTH_SHORT).show();
    }

    /**  Khi người dùng tick chọn / bỏ chọn sản phẩm */
    @Override
    public void onItemCheckedChanged(List<CartItem> selectedItems) {
        updateTotalPrice();
    }

    /**  Khi tăng/giảm số lượng */
    @Override
    public void onQuantityChanged(CartItem item) {
        if (currentCart == null) return;

        // Chỉ cập nhật sản phẩm đó, không reload toàn trang
        cartRepository.addOrUpdateItem(currentCart.getCartId(), item, new DataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // chỉ cập nhật lại giá tổng, không load lại
                updateTotalPrice();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Cập nhật thất bại: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
