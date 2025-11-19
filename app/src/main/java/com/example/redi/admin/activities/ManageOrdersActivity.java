package com.example.redi.admin.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import com.example.redi.R;
import com.example.redi.common.base.BaseAdminActivity;
import com.example.redi.admin.fragments.OrdersFragment;

public class ManageOrdersActivity extends BaseAdminActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Quản lý đơn hàng");
        getLayoutInflater().inflate(R.layout.admin_manage_orders_main,
                findViewById(R.id.admin_fragment_container), true);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.admin_fragment_container, new OrdersFragment());
            ft.commit();
        }
    }
}
