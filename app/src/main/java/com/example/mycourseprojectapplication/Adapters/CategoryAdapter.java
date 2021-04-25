package com.example.mycourseprojectapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycourseprojectapplication.Models.Category;
import com.example.mycourseprojectapplication.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final ArrayList<Category> categories;          // declare our views
    private View.OnClickListener onClickListener;


    public CategoryAdapter(ArrayList<Category> categories) {        // adapter constructor
        this.categories = categories;
    }

    public class ViewHolder extends RecyclerView.ViewHolder   // view holder describes item view in the recycler view
    {
        public ImageView imageView;            // declare our variable for views
        public TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView4);
            textView = itemView.findViewById(R.id.textViewCategroyTitle);     // initialize our views
            itemView.setTag(this); // set a tag for click listener
            itemView.setOnClickListener(onClickListener); // set click listener
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // bind the data
        Category category = categories.get(position); // get individual category
        holder.imageView.setImageResource(category.getRes_img()); // set image for category
        holder.textView.setText(category.getTitle()); // set title for category
    }

    @Override
    public int getItemCount() {
        return categories.size(); // return size of categories
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {     // setter for on click listener
        this.onClickListener = onClickListener;
    }
}
