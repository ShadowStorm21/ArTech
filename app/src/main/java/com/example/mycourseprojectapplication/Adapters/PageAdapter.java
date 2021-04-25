package com.example.mycourseprojectapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mycourseprojectapplication.Fragments.AdminCustomerSupportFragment;
import com.example.mycourseprojectapplication.Fragments.AdminHomeFragment;
import com.example.mycourseprojectapplication.Fragments.AdminProductsFragment;

public class PageAdapter extends FragmentStatePagerAdapter {

    public PageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {        // get the fragment item
        switch (position) // based on position change fragment
        {
            case 0 : return new AdminHomeFragment();
            case 1 : return new AdminProductsFragment();
            case 2 : return new AdminCustomerSupportFragment();
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 3; // numbers of fragments
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { // set the title of each fragment

        switch (position)
        {
            case 0 : return "Orders";
            case 1 : return "Products";
            case 2 : return "Service";
            default: return null;

        }
    }
}
