package com.example.mycourseprojectapplication.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mycourseprojectapplication.Adapters.PageAdapter;
import com.example.mycourseprojectapplication.Fragments.ReportTypeFragment;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ExtendedFloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);


        // TODO: Add a reenter transition in the backwards direction to animate
        // ActivityB out and ActivityA back in in the opposite direction.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Control Center");
        toolbar.setTitleTextColor(Color.DKGRAY);
        PageAdapter sectionsPagerAdapter = new PageAdapter(getSupportFragmentManager(), PagerAdapter.POSITION_NONE);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.admin_logout:   // on cart icon click go to cart activity
                mAuth.signOut();
                Intent intent = new Intent(AdminActivity.this,LoginActivity.class);
                startActivity(intent);
                finishAffinity();
                break;
            case R.id.report:
                ReportTypeFragment reportTypeFragment = ReportTypeFragment.newInstance();
                reportTypeFragment.show(getSupportFragmentManager(),"ModalSheet");
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() { // on back pressed show an alert before exiting the application
        new MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Are you sure you want to close the application")
                .setCancelable(true)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();

            }
        }).show();


    }
}