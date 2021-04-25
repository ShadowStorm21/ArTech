package com.example.mycourseprojectapplication.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mycourseprojectapplication.Adapters.AddressPageAdapter;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.tabs.TabLayout;

import ru.alexbykov.nopermission.PermissionHelper;

public class AddressActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Toolbar toolbar = findViewById(R.id.toolbarAddress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Your Address");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AddressPageAdapter sectionsPagerAdapter = new AddressPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager = findViewById(R.id.view_pager_address);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabsAddress);
        tabs.setupWithViewPager(viewPager);




    }


    @Override
    protected void onResume() {
        super.onResume();

    }





    public void goToFirstPage()
    {
        viewPager.setCurrentItem(0);
    }



}