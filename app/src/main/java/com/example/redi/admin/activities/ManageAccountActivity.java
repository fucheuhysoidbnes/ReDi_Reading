package com.example.redi.admin.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.redi.R;
import com.example.redi.common.base.BaseAdminActivity;
import com.example.redi.admin.fragments.UsersFragment;

public class ManageAccountActivity extends BaseAdminActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Quản lý tài khoản");
        //  CHỈ INFLATE layout này vào vùng nội dung admin
        getLayoutInflater().inflate(
                R.layout.activity_manage_account,
                findViewById(R.id.admin_fragment_container),
                true
        );

        //  Show UsersFragment mặc định
        if (savedInstanceState == null) {
            openFragment(new UsersFragment(), false);
        }
    }

    public void openFragment(Fragment f, boolean addToBackstack) {
        FragmentManager fm = getSupportFragmentManager();
        if (addToBackstack) {
            fm.beginTransaction()
                    .replace(R.id.admin_fragment_container, f)
                    .addToBackStack(null)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.admin_fragment_container, f)
                    .commit();
        }
    }

}
