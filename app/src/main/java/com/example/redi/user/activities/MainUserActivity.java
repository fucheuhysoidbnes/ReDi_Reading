package com.example.redi.user.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.redi.R;
import com.example.redi.user.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainUserActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        bottomNav = findViewById(R.id.bottomNavigationUser);

        // Load HomeFragment mặc định
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            if (item.getItemId() == R.id.menu_home) {
                selected = new HomeFragment();
            }
            // Sau này thêm SearchFragment, AccountFragment...

            return loadFragment(selected);
        });
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
