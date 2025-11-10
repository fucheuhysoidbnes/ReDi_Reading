package com.example.redi.user.activities;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.example.redi.R;
import com.example.redi.common.base.BaseUserActivity;
import com.example.redi.user.fragments.CartFragment;

public class CartActivity extends BaseUserActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_cart_main);
        setupBottomNavigation(R.id.menu_cart);

        // Gắn tag cho CartFragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.containerUser, new CartFragment(), "CartFragment");
        ft.commit();
    }
}
