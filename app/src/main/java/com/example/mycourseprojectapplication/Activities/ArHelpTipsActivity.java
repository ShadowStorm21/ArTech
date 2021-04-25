package com.example.mycourseprojectapplication.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mycourseprojectapplication.Adapters.ArHelpPager;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.tabs.TabLayout;

public class ArHelpTipsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ar_control);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("AR Help Tips");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArHelpPager pagerAdapter = new ArHelpPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager = findViewById(R.id.view_pager_tips);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabs = findViewById(R.id.tabsTips);
        tabs.setupWithViewPager(viewPager);
    }
}