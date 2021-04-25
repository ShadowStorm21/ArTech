package com.example.mycourseprojectapplication.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class ProductFilterActivity extends AppCompatActivity {
    private ChipGroup chipGroup;
    private String filter ="";
    private String filterBrand = "";
    private UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);

        userSession = new UserSession(this);
        RadioGroup radioGroupFilter = findViewById(R.id.radioGroupFilterSort);
        filter = "Ascending";
        radioGroupFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioButtonProductAsce)
                {
                    filter = "Ascending";
                }
                if(checkedId == R.id.radioButtonProductDesc)
                {
                    filter = "Descending";
                }
                if(checkedId == R.id.radioButtonLowToHigh)
                {
                    filter= "LTH";
                }
                if(checkedId == R.id.radioButtonHighToLow)
                {
                    filter = "HTL";
                }
            }
        });

        Bundle bundle = getIntent().getBundleExtra("brands");
        Category category = (Category) bundle.getSerializable("category");
        if(bundle != null) {
            String[] brands = bundle.getStringArray("brands");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 8, 8, 8);
            chipGroup = findViewById(R.id.chipGroup);
            for (int i = 0; i < brands.length; i++) {
                Chip chip = new Chip(this);
                chip.setText(brands[i]);
                chip.setCheckable(true);
                chipGroup.addView(chip);

            }
        }

        Toast.makeText(this, userSession.getBrandProductFilter()+"", Toast.LENGTH_SHORT).show();

        if(userSession.getFilterTypeProducts() == null)
        {
            radioGroupFilter.clearCheck();
        }
        else
        {
            for(int i = 0; i < radioGroupFilter.getChildCount(); i++)
            {
                RadioButton radioButton = (RadioButton) radioGroupFilter.getChildAt(i);
                radioButton.setChecked(true);

            }
        }
        if(userSession.getBrandProductFilter() == null)
        {
            //chipGroup.clearCheck();
        }
        else {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(true);

                if (chip.getText().toString().equals(userSession.getBrandProductFilter())) {

                    chip.setChecked(true);

                } else {
                    chip.setChecked(true);
                }

            }
        }

        Button buttonApply = findViewById(R.id.buttonSortProductApply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> brands = new ArrayList<>();
                for(int i = 0; i < chipGroup.getChildCount(); i++)
                {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    Log.i("outside if ", i+ " chip = " + chip.getText().toString());
                    if (chip.isChecked()) {
                        Log.i("inside if ", i + " chip = " + chip.getText().toString());
                        filterBrand = chip.getText().toString();

                    }

                }


                Intent returnIntent = new Intent();
                returnIntent.putExtra("categroy",category);
                returnIntent.putExtra("brand",filterBrand);
                returnIntent.putExtra("filter",filter);
                userSession.setFilterSwitchStateProducts(true);
                userSession.setFilterTypeProducts(filter);
                userSession.setProductBrandFilter(filterBrand);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
        });

    }
}