package com.example.redi.user.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redi.R;

public class PdfPreviewFragment extends Fragment {

    private String pdfUrl;
    private WebView webView;
    private ProgressBar progressBar;

    public static PdfPreviewFragment newInstance(String url) {
        PdfPreviewFragment f = new PdfPreviewFragment();
        Bundle b = new Bundle();
        b.putString("pdfUrl", url);
        f.setArguments(b);
        return f;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_pdfpreview, container, false);
        webView = v.findViewById(R.id.webView);
        progressBar = v.findViewById(R.id.progressBar);
        ImageButton btnBack = v.findViewById(R.id.btnBack);

        if (getArguments() != null)
            pdfUrl = getArguments().getString("pdfUrl");

        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Không có file PDF", Toast.LENGTH_SHORT).show();
            return v;
        }

        // Nếu link Google Drive thì chuyển sang direct preview link
        if (pdfUrl.contains("drive.google.com")) {
            pdfUrl = pdfUrl.replace("/view?", "/preview?");
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        // Dùng Google Docs Viewer để hiển thị PDF
        String googleViewer = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
        webView.loadUrl(googleViewer);

        btnBack.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
        return v;
    }
}
