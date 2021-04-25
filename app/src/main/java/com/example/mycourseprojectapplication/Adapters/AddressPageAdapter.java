package com.example.mycourseprojectapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mycourseprojectapplication.Fragments.AutoAddressFragment;
import com.example.mycourseprojectapplication.Fragments.ManualAddressFragment;

public class AddressPageAdapter extends FragmentPagerAdapter {

    public AddressPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {  // get the fragment item
        switch (position) // based on position change fragment
        {
            case 0 : return new ManualAddressFragment();
            case 1 : return new AutoAddressFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // numbers of fragments
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { // set the title of each fragment
        switch (position)
        {
            case 0 : return "Manual";
            case 1 : return "Auto";
            default: return null;
        }
    }
}
