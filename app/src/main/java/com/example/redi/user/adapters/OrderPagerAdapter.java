package com.example.redi.user.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.redi.common.utils.Constants;
import com.example.redi.user.fragments.OrdersListFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return OrdersListFragment.newInstance(Constants.STATUS_PENDING);
        else if (position == 1)
            return OrdersListFragment.newInstance(Constants.STATUS_DELIVERING);
        else
            return OrdersListFragment.newInstance(Constants.STATUS_RECEIVED);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
