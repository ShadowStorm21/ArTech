package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Activities.AddProductActivity;
import com.example.mycourseprojectapplication.Activities.AdminCategoriesActivity;
import com.example.mycourseprojectapplication.Adapters.CategoryAdapter;
import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;


public class AdminProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;          // declare our variables
    private ArrayList<Category> categories;

    public AdminProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_admin_products, container, false);
       recyclerView = view.findViewById(R.id.product_admin_category);
       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
       recyclerView.setLayoutManager(layoutManager);
       categoryAdapter = new CategoryAdapter(categories);
       recyclerView.setAdapter(categoryAdapter);
       categoryAdapter.setOnClickListener(onClickListener);
       FrameLayout frameLayout = view.findViewById(R.id.conainter_admin_product);
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getContext(), AddProductActivity.class);
                startActivity(intent);


            }
        });


       addCategories();
       return view;
    }

    private void addCategories()  // method to add all categories
    {
        Category SmartPhones = new Category("Smartphones",R.drawable.ic_baseline_smartphone_24);
        Category Laptop = new Category("Laptops",R.drawable.ic_baseline_laptop_24);
        Category TV = new Category("TV's",R.drawable.ic_baseline_tv_24);
        Category headsets = new Category("Headsets",R.drawable.ic_outline_headset_24);
        Category SmartWatches = new Category("Smartwatches",R.drawable.ic_outline_watch_24);           // create categories and add them then notify the adapter
        Category Cameras = new Category("Cameras",R.drawable.ic_outline_camera_alt_24);
        categories.add(SmartPhones);
        categories.add(Laptop);
        categories.add(TV);
        categories.add(headsets);
        categories.add(SmartWatches);
        categories.add(Cameras);
        categoryAdapter.notifyDataSetChanged();
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() { // on category item clicked
        @Override
        public void onClick(View v) {


            CategoryAdapter.ViewHolder viewHolder = (CategoryAdapter.ViewHolder) v.getTag(); // get the tag
            int position = viewHolder.getAdapterPosition();                                   // get the adapter position
            Intent intent = new Intent(getContext(), AdminCategoriesActivity.class); // go that category activity
            intent.putExtra("categroy",  categories.get(position)); // bundle category info
            startActivity(intent); // start activity


        }
    };
}