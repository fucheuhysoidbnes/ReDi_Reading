package com.example.redi.user.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.redi.R;

public class PdfPreviewFragment extends Fragment {

    private String pdfUrl;
    private ImageButton btnBack, btnShare;

    public static PdfPreviewFragment newInstance(String url) {
        PdfPreviewFragment fragment = new PdfPreviewFragment();
        Bundle args = new Bundle();
        args.putString("pdfUrl", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.client_fragment_pdfpreview, container, false);
        btnBack = v.findViewById(R.id.btnBack);
        btnShare = v.findViewById(R.id.btnShare);

        if (getArguments() != null)
            pdfUrl = getArguments().getString("pdfUrl");

        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Không có file PDF", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return v;
        }

        // Mở PDF ngay bằng Chrome Custom Tab
        openPdfInChromeTab(pdfUrl);

        // Quay lại
        btnBack.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());

        // Nút chia sẻ file PDF
        btnShare.setOnClickListener(view -> sharePdfLink(pdfUrl));

        return v;
    }

    private void openPdfInChromeTab(String url) {
        try {
            // Nếu là file PDF ngoài Drive thì dùng Google Docs Viewer để hiển thị
            if (url.endsWith(".pdf") && !url.contains("drive.google.com")) {
                url = "https://docs.google.com/gview?embedded=true&url=" + url;
            }

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setUrlBarHidingEnabled(true);
            builder.setShareState(CustomTabsIntent.SHARE_STATE_ON);

            //  Đồng bộ màu thanh Chrome với màu app
            builder.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.background));

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url));

            // Đóng fragment ngay sau khi mở tab (fix lỗi trang trắng khi quay lại)
            requireActivity().getSupportFragmentManager().popBackStack();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Không thể mở file PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdfLink(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ tài liệu");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, "Chia sẻ qua"));
    }
}
