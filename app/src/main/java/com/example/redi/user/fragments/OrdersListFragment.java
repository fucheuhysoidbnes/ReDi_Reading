package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.common.models.Order;
import com.example.redi.common.utils.UserSession;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;
import com.example.redi.user.adapters.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrdersListFragment extends Fragment {

    private static final String ARG_STATUS = "status";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private OrderAdapter adapter;
    private OrderRepository repo;
    private final List<Order> orders = new ArrayList<>();
    private String filterStatus = "";

    public static OrdersListFragment newInstance(String status) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.client_fragment_orders_list, container, false);

        recyclerView = v.findViewById(R.id.recyclerOrders);
        progressBar = v.findViewById(R.id.progressBarOrders);
        tvEmpty = v.findViewById(R.id.tvEmptyOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        repo = new OrderRepository();

        if (getArguments() != null)
            filterStatus = getArguments().getString(ARG_STATUS, "");

        adapter = new OrderAdapter(orders, filterStatus, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(adapter);

        loadOrders();
        return v;
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        String uid = new UserSession(requireContext()).getCurrentUser().getId();

        repo.listenOrdersByUser(uid, new DataSourceCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> result) {
                progressBar.setVisibility(View.GONE);
                orders.clear();

                for (Order o : result) {
                    if (filterStatus.equals(o.getStatus()))
                        orders.add(o);
                }

                adapter.notifyDataSetChanged();
                if (orders.isEmpty())
                    tvEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi tải đơn hàng: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        repo.removeListeners();
    }
}
