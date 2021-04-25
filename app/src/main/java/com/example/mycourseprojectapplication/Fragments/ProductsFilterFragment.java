package com.example.mycourseprojectapplication.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.example.mycourseprojectapplication.Activities.IndividualCategoryProducts;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class ProductsFilterFragment extends BottomSheetDialogFragment  {

    private ChipGroup chipGroup;
    private String filter ="";
    private String filterBrand = "";
    private int index;
    private UserSession userSession;
    public ProductsFilterFragment() {
        // Required empty public constructor
    }


    public static ProductsFilterFragment newInstance() {
        ProductsFilterFragment fragment = new ProductsFilterFragment();
        return fragment;
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSession = new UserSession(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_filter, container, false);
        RadioGroup radioGroupFilter = view.findViewById(R.id.radioGroupFilterSort);
        chipGroup = view.findViewById(R.id.chipGroup);


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
                    filter= "Price: Low to High";
                }
                if(checkedId == R.id.radioButtonHighToLow)
                {
                    filter = "Price: High to Low";
                }
            }
        });


       Bundle bundle = getArguments();
       if(bundle != null) {
           String[] brands = bundle.getStringArray("brands");
           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           params.setMargins(8, 8, 8, 8);

           for (int i = 0; i < brands.length; i++) {
               Chip chip = new Chip(getContext());
               chip.setChipBackgroundColorResource(R.color.colorPrimary);
               chip.setTextColor(ContextCompat.getColor(getContext(),R.color.fullblack));
               chip.setText(brands[i]);
               chip.setCheckable(true);
               chipGroup.addView(chip);

           }

           if(userSession.getFilterTypeProducts() == null)
           {
               radioGroupFilter.clearCheck();
           }
           else
           {
               for(int i = 0; i < radioGroupFilter.getChildCount(); i++)
               {
                   RadioButton radioButton = (RadioButton) radioGroupFilter.getChildAt(i);
                   if(radioButton.getText().toString().equals(userSession.getFilterTypeProducts()))
                   {
                       radioButton.setChecked(true);
                   }
                   else
                   {
                       radioButton.setChecked(false);
                   }
               }
           }
           if(userSession.getBrandProductFilter() == null)
           {
               chipGroup.clearCheck();
           }
           else
           {
               for(int i = 0; i < chipGroup.getChildCount(); i++)
               {
                   Chip chip = (Chip) chipGroup.getChildAt(i);

                   if (chip.getText().toString().equals(userSession.getBrandProductFilter())) {

                       chip.setChecked(true);
                   }
                   else {
                       chip.setChecked(false);
                   }

               }
           }

       }



        Button buttonApply = view.findViewById(R.id.buttonSortProductApply);
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
                  userSession.setFilterSwitchStateProducts(true);
                    userSession.setFilterTypeProducts(filter);
                    userSession.setProductBrandFilter(filterBrand);
                    if(filterBrand.isEmpty())
                    {
                        if(filter.length() > 0)
                        {
                            ((IndividualCategoryProducts) getActivity()).getProductsSorted(filter);
                        }
                        else
                        {
                            userSession.setFilterSwitchStateProducts(false);
                            userSession.setFilterTypeProducts("Default");
                            userSession.setProductBrandFilter("Default");
                            ((IndividualCategoryProducts) getActivity()).getProducts();
                            dismiss();
                        }
                    }
                    else
                    {
                        ((IndividualCategoryProducts) getActivity()).getProductsFiltered(filterBrand,filter);
                    }
               dismiss();

           }
       });

       Button buttonClear = view.findViewById(R.id.buttonClearFilterProducts);
       buttonClear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               userSession.setFilterSwitchStateProducts(false);
               userSession.setFilterTypeProducts("Default");
               userSession.setProductBrandFilter("Default");
               ((IndividualCategoryProducts) getActivity()).getProducts();
               dismiss();
           }
       });
        return view;
    }



}