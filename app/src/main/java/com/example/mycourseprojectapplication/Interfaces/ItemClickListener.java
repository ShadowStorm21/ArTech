package com.example.mycourseprojectapplication.Interfaces;

import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Adapters.ProductAdapter;
import com.example.mycourseprojectapplication.Adapters.RecentProductAdapter;

public interface ItemClickListener {

    void onItemClick(RecyclerView.ViewHolder myViewHolder, int pos, ImageView imageView);
}
