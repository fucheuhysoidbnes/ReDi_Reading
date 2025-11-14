package com.example.redi.admin.activities;

import android.os.Bundle;

import com.example.redi.R;
import com.example.redi.admin.fragments.DashboardFragment;
import com.example.redi.common.base.BaseAdminActivity;

public class MainAdminActivity extends BaseAdminActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openFragment(new DashboardFragment());
    }

    private void openFragment(DashboardFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }
}
