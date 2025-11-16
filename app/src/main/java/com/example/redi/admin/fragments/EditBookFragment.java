package com.example.redi.admin.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.webkit.ValueCallback;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.redi.R;
import com.example.redi.admin.viewmodel.AdminBookViewModel;
import com.example.redi.common.models.Book;
import com.example.redi.common.utils.DriveUtils;

public class EditBookFragment extends Fragment {

    // --- Tạo fragment với dữ liệu ---
    public static EditBookFragment newInstance(Book book) {
        EditBookFragment fragment = new EditBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    private EditText edtTitle, edtPrice, edtQty, edtImg, edtContent, edtDesc;
    private ImageView imgPreview, imgPdfPreview;

    private AdminBookViewModel viewModel;
    private Book editingBook;

    private OnBackPressedCallback backCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_edit_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        // --- Callback BACK (fix loop) ---
        backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        };

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backCallback);

        // --- ViewModel ---
        viewModel = new ViewModelProvider(requireActivity()).get(AdminBookViewModel.class);

        // --- Khởi tạo view ---
        imgPreview = v.findViewById(R.id.imgPreview);
        imgPdfPreview = v.findViewById(R.id.imgPdfPreview);

        edtTitle = v.findViewById(R.id.edtTitle);
        edtDesc = v.findViewById(R.id.edtDescription);
        edtPrice = v.findViewById(R.id.edtPrice);
        edtQty = v.findViewById(R.id.edtQuantity);
        edtImg = v.findViewById(R.id.edtImageUrl);
        edtContent = v.findViewById(R.id.edtContentUrl);

        v.findViewById(R.id.btnBack).setOnClickListener(x -> confirmExit());
        v.findViewById(R.id.btnCancel).setOnClickListener(x -> confirmExit());
        imgPdfPreview.setOnClickListener(x -> openPdf());

        // --- Edit mode ---
        if (getArguments() != null) {
            editingBook = (Book) getArguments().getSerializable("book");
            if (editingBook != null) fillData();
        }

        // --- Tự preview ảnh & pdf ---
        edtImg.addTextChangedListener(simpleWatcher(this::loadImage));
        edtContent.addTextChangedListener(simpleWatcher(s -> loadPdfPreview()));

        v.findViewById(R.id.btnSave).setOnClickListener(x -> confirmSave());
    }

    private TextWatcher simpleWatcher(ValueCallback<String> callback) {
        return new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                callback.onReceiveValue(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence c, int i, int i1, int i2) {}
        };
    }

    private void fillData() {
        edtTitle.setText(editingBook.getTitle());
        edtDesc.setText(editingBook.getDescription());
        edtPrice.setText(String.valueOf(editingBook.getPrice()));
        edtQty.setText(String.valueOf(editingBook.getQuantity()));
        edtImg.setText(editingBook.getImageUrl());
        edtContent.setText(editingBook.getContent());

        loadImage(editingBook.getImageUrl());
        loadPdfPreview();
    }

    private void loadImage(String url) {
        if (url == null || url.isEmpty()) return;

        String fixed = DriveUtils.getDirectImageLink(url);

        Glide.with(requireContext())
                .load(fixed)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(imgPreview);
    }

    private void loadPdfPreview() {
        imgPdfPreview.setImageResource(R.drawable.ic_pdf);
    }

    private void openPdf() {
        String url = edtContent.getText().toString().trim();
        if (url.isEmpty()) return;

        url = DriveUtils.getPdfPreviewLink(url);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.background));

        CustomTabsIntent tab = builder.build();
        tab.launchUrl(requireContext(), android.net.Uri.parse(url));
    }

    private void confirmSave() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Lưu sách")
                .setMessage("Bạn có chắc muốn lưu?")
                .setPositiveButton("Lưu", (d, w) -> saveBook())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveBook() {
        Book b = (editingBook != null) ? editingBook : new Book();

        b.setTitle(edtTitle.getText().toString().trim());
        b.setDescription(edtDesc.getText().toString().trim());
        b.setPrice(Integer.parseInt(edtPrice.getText().toString().trim()));
        b.setQuantity(Integer.parseInt(edtQty.getText().toString().trim()));
        b.setImageUrl(DriveUtils.getDirectImageLink(edtImg.getText().toString().trim()));
        b.setContent(DriveUtils.getPdfPreviewLink(edtContent.getText().toString().trim()));

        if (editingBook == null)
            viewModel.addBook(b);
        else
            viewModel.updateBook(b);

        backCallback.setEnabled(false);
        requireActivity().onBackPressed();
    }

    private void confirmExit() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Thoát")
                .setMessage("Bạn có chắc muốn thoát mà không lưu?")
                .setPositiveButton("Thoát", (d, w) -> {
                    backCallback.setEnabled(false);
                    requireActivity().onBackPressed();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
