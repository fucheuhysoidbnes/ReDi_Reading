package com.example.redi.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.common.models.Book;
import com.example.redi.common.models.Cart;
import com.example.redi.common.models.CartItem;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.CartRepository;
import com.example.redi.user.fragments.PdfPreviewFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BookDetailFragment extends Fragment {
    private ImageView ivCover;
    private TextView tvTitle, tvPrice, tvDescription, tvQty;
    private ImageButton btnMinus, btnPlus;
    private Button btnAddCart, btnBuyNow, btnRead;
    private Book book;
    private int qty = 1;

    private CartRepository cartRepo;

    public static BookDetailFragment newInstance(Book book) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_bookdetail, container, false);

        ivCover = v.findViewById(R.id.ivCover);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvPrice = v.findViewById(R.id.tvPrice);
        tvDescription = v.findViewById(R.id.tvDescription);
        tvQty = v.findViewById(R.id.tvQty);
        btnMinus = v.findViewById(R.id.btnMinus);
        btnPlus = v.findViewById(R.id.btnPlus);
        btnAddCart = v.findViewById(R.id.btnAddCart);
        btnBuyNow = v.findViewById(R.id.btnBuyNow);
        btnRead = v.findViewById(R.id.btnRead);

        cartRepo = new CartRepository();

        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
            setupUI();
        }

        btnMinus.setOnClickListener(view -> { if (qty > 1) tvQty.setText(String.valueOf(--qty)); });
        btnPlus.setOnClickListener(view -> tvQty.setText(String.valueOf(++qty)));
        btnAddCart.setOnClickListener(view -> addToCart());
        btnRead.setOnClickListener(view -> openPdf());
        btnBuyNow.setOnClickListener(view -> Toast.makeText(requireContext(), "Chức năng thanh toán sẽ thêm sau", Toast.LENGTH_SHORT).show());

        return v;
    }

    private void setupUI() {
        tvTitle.setText(book.getTitle());
        tvPrice.setText(String.format("%,d VNĐ", book.getPrice()));
        tvDescription.setText(book.getDescription());
        Glide.with(this).load(book.getImageUrl()).into(ivCover);
    }

    private void addToCart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();
        cartRepo.findCartByUser(uid, new DataSourceCallback<Cart>() {
            @Override public void onSuccess(Cart cart) {
                if (cart == null) {
                    cartRepo.createCart(uid, new DataSourceCallback<Cart>() {
                        @Override public void onSuccess(Cart newCart) { pushItem(newCart.getCartId()); }
                        @Override public void onError(String error) { Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show(); }
                    });
                } else pushItem(cart.getCartId());
            }
            @Override public void onError(String error) { Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void pushItem(String cartId) {
        CartItem item = new CartItem(book.getBook_id(), book.getTitle(), book.getImageUrl(), book.getPrice(), qty);
        cartRepo.addOrUpdateItem(cartId, item, new DataSourceCallback<Void>() {
            @Override public void onSuccess(Void data) {
                Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPdf() {
        if (book.getContent() == null || book.getContent().isEmpty()) {
            Toast.makeText(requireContext(), "Không có nội dung đọc thử", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ chuyển sang PdfPreviewFragment để hiển thị trong MainUserActivity
        PdfPreviewFragment fragment = PdfPreviewFragment.newInstance(book.getContent());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerUser, fragment)
                .addToBackStack(null)
                .commit();
    }

}
