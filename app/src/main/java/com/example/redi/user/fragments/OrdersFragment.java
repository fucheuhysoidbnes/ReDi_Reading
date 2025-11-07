package com.example.redi.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.user.adapters.OrdersAdapter;
import com.example.redi.user.data.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerOrders;
    private View tvEmptyOrders;
    private OrdersAdapter adapter;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_orders_main, container, false);

        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);

        orderList = new ArrayList<>();
//        orderList.add(new Order("#001", "user123", "07/11/2025", "150.000đ",
//                "Đang giao", "Thanh toán khi nhận hàng", "Hà Nội, Việt Nam"));
//
//        orderList.add(new Order("#002", "user123", "03/11/2025", "250.000đ",
//                "Đã giao thành công", "Chuyển khoản", "TP.HCM, Việt Nam"));
//
//        orderList.add(new Order("#003", "user123", "01/11/2025", "99.000đ",
//                "Đã thanh toán", "Momo", "Đà Nẵng, Việt Nam"));


        if (orderList.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            recyclerOrders.setVisibility(View.GONE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            recyclerOrders.setVisibility(View.VISIBLE);
            recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new OrdersAdapter(orderList);
            recyclerOrders.setAdapter(adapter);
        }

        return view;
    }
}
