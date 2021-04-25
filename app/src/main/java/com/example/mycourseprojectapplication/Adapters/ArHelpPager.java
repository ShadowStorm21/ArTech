package com.example.mycourseprojectapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mycourseprojectapplication.Fragments.ColorFragment;
import com.example.mycourseprojectapplication.Fragments.RotateFragment;
import com.example.mycourseprojectapplication.Fragments.ScaleFragment;

public class ArHelpPager extends FragmentPagerAdapter {

    public ArHelpPager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) // based on position change fragment
        {
            case 0 : return new ColorFragment();
            case 1 : return new ScaleFragment();
            case 2 : return new RotateFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { // set the title of each fragment
        switch (position)
        {
            case 0 : return "Colors";
            case 1 : return "Scale";
            case 2 : return "Rotate";
            default: return null;
        }
    }
}
