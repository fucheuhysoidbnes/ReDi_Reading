package com.example.redi.user.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.redi.R;
import com.example.redi.common.base.BaseUserActivity;
import com.example.redi.common.utils.Constants;
import com.example.redi.common.utils.UserSession;
import com.example.redi.user.adapters.OrderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrdersActivity extends BaseUserActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvLoginNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_orders_main);

        // Ánh xạ view
        tabLayout = findViewById(R.id.tabLayoutOrders);
        viewPager = findViewById(R.id.viewPagerOrders);
        tvLoginNotice = findViewById(R.id.tvLoginNotice);

        UserSession session = new UserSession(this);

        // Nếu chưa đăng nhập
        if (session.getCurrentUser() == null) {
            tvLoginNotice.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            tvLoginNotice.setText("Bạn cần đăng nhập để xem đơn hàng.");
            setupBottomNavigation(R.id.menu_orders);
            return;
        }

        // Nếu đã đăng nhập → hiển thị ViewPager + Tab
        tvLoginNotice.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);

        viewPager.setAdapter(new OrderPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(Constants.STATUS_PENDING);
            } else if (position == 1) {
                tab.setText(Constants.STATUS_DELIVERING);
            } else if (position == 2) {
                tab.setText(Constants.STATUS_RECEIVED);
            }
        }).attach();

        setupBottomNavigation(R.id.menu_orders);
    }
}
