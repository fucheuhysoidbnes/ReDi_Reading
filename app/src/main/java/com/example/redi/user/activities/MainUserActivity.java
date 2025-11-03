package com.example.redi.user.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.redi.R;
import com.example.redi.user.fragments.HomeFragment;
import com.example.redi.common.base.BaseUserActivity;

public class MainUserActivity extends BaseUserActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_home_main);

        setupBottomNavigation(R.id.menu_home); // truyền ID menu hiện tại
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerUser, new HomeFragment())
                .commit();
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerUser, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
