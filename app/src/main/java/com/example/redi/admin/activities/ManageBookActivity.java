package com.example.redi.admin.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.redi.R;
import com.example.redi.admin.fragments.BookListFragment;
import com.example.redi.common.base.BaseAdminActivity;

public class ManageBookActivity extends BaseAdminActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Quản lý sách");

        getLayoutInflater().inflate(
                R.layout.admin_manage_book_main,
                findViewById(R.id.admin_fragment_container),
                true
        );

        if (savedInstanceState == null) {
            openFragment(new BookListFragment(), false);
        }
    }

    public void openFragment(Fragment f, boolean addBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );

        ft.replace(R.id.admin_fragment_container, f);

        if (addBackStack) ft.addToBackStack(null);

        ft.commit();
    }

}
