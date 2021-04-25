package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Rating;
import com.example.mycourseprojectapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>  {


    public ArrayList<Products> productsArrayList = new ArrayList<>();
    public ArrayList<Products> filteredProducts = new ArrayList<>();
    private Context context;
    private ItemClickListener itemClickListener;
    private ArrayList<Rating> ratingArrayList = new ArrayList<>();           // declare our variables
    private double total;
    private double avg;
    private double rate;


    public ProductAdapter(ArrayList<Products> productsArrayList, ItemClickListener itemClickListener) {       // adapter constructor
        this.productsArrayList = productsArrayList;
        this.itemClickListener = itemClickListener;
        this.filteredProducts = productsArrayList;
    }


    /*@Override
    public Filter getFilter() {
        return new MyFilter(this,productsArrayList);
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder // view holder describes item view in the recycler view
    {
        public TextView productName, productBrand, productPrice, textViewReviewCount;
        public ImageView productImage;               // declare our variables
        public RatingBar ratingBar;
        public LinearLayout linearLayout;
        public ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.textViewProductNameRecent);
            productBrand = itemView.findViewById(R.id.textViewProductBrandRecent);
            productPrice = itemView.findViewById(R.id.textViewProductPriceRecent);         // initialize our views
            productImage = itemView.findViewById(R.id.productImageRecent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewReviewCount = itemView.findViewById(R.id.textViewRatingCount);
            progressBar = itemView.findViewById(R.id.progressBarLoadImage);
            linearLayout = itemView.findViewById(R.id.card);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {       // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) { // bind the data
        Products product = productsArrayList.get(position); // get individual product
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.productImage.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.INVISIBLE);
                return false;
            }
        }).into(holder.productImage);// load the image from the url into product image
        holder.productName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(product.getProductPrice());
        holder.productPrice.setText("$" + price);  // set the product details
        holder.productBrand.setText(product.getProductBrand());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reviews");

        myRef.child(product.getProduct_id()).addValueEventListener(new ValueEventListener() {    // get the reviews associated with the product
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot reviews : snapshot.getChildren()) {
                    ratingArrayList.add(reviews.getValue(Rating.class));

                    if (reviews.child("ratting") != null) {  // check rating is not null

                        try {
                            rate = (double) reviews.child("ratting").getValue();       // get the rating value
                            total = total + rate;                // add rate to total
                            i++;   // increase counter
                            avg = total / i;     // get avg
                            holder.textViewReviewCount.setText("( " + i + " )"); // set number of reviews
                            holder.ratingBar.setRating((float) avg); // set rating
                        } catch (Exception e) {
                            rate = (long) reviews.child("ratting").getValue();  // get the rating value but in long
                            total = total + rate;  // add rate to total
                            i++; // increase counter
                            holder.textViewReviewCount.setText("( " + i + " )");   // set number of reviews
                            avg = total / i;// get avg
                            holder.ratingBar.setRating((float) avg); // set rating
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewCompat.setTransitionName(holder.productImage, product.getProductName()); // set the transition name for the image for (Image transition purpose)
        holder.itemView.setOnClickListener(new View.OnClickListener() { // on item clicked
            @Override
            public void onClick(View v) {

                itemClickListener.onItemClick(holder, holder.getAdapterPosition(), holder.productImage); // call the interface method on item click and pass the holder and position and product image
            }
        });

    }



    @Override
    public int getItemCount() {
        return productsArrayList.size(); // return the size of array list
    }






    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        productsArrayList.clear();
        if (charText.length() == 0) {
            productsArrayList.addAll(filteredProducts);
        }
        else
        {
            for (Products item : filteredProducts) {
                if (item.getProductBrand().toLowerCase(Locale.getDefault()).equals(charText)) {
                    productsArrayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


 /*  public class MyFilter extends Filter
    {
        private final ProductAdapter adapter;

        private final ArrayList<Products> originalList;

        private final ArrayList<Products> filteredList;



        public MyFilter(ProductAdapter adapter, ArrayList<Products> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new ArrayList<>(originalList);
            this.filteredList = new ArrayList<>();
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String[] parts = ((String) constraint).split("-");
            String brand = parts[0];
            String type = parts[1];
            filteredProducts.clear();
            final FilterResults results = new FilterResults();
            if(brand.length() == 0){
                filteredProducts.addAll(productsArrayList);
            }else{
                final String filterPattern = brand.toString().toLowerCase().trim();
                for(Products item: productsArrayList) {
                    if(item.getProductBrand().toLowerCase().equals(filterPattern)){ // replace this condition with actual you need
                       filteredProducts.add(item);
                    }
                }
            }
            results.values = filteredProducts;
            results.count =filteredProducts.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredProducts.clear();
            adapter.filteredProducts.addAll((Collection<? extends Products>) results.values);
            String[] parts = ((String) constraint).split("-");
            String brand = parts[0];
            String type = parts[1];

            adapter.notifyDataSetChanged();

        }
    }

/*
    public void sort(final Boolean ascending, final String property){
        String[] parts = property.split("-");
        String brand = parts[0];
        String type = parts[1];
        sortedList = new SortedList<Products>(Products.class, new SortedList.Callback<Products>() {
            @Override
            public int compare(Products o1, Products o2) {
                if(ascending){
                    if(property.equalsIgnoreCase("LTH")){
                        return String.valueOf(o1.getProductBrand()).compareTo(brand);
                    }else if(property.equalsIgnoreCase("HTL")){
                        return String.valueOf(star1.getFavorites()).compareTo(String.valueOf(star2.getFavorites()));
                    }else if(property.equalsIgnoreCase("VIEWS")){
                        return String.valueOf(star1.getViews()).compareTo(String.valueOf(star2.getViews()));
                    }
                    return star1.getName().compareTo(star2.getName());
                }else{
                    if(property.equalsIgnoreCase("COMMENTS")){
                        return String.valueOf(star2.getComments()).compareTo(String.valueOf(star1.getComments()));
                    }else if(property.equalsIgnoreCase("FAVORITES")){
                        return String.valueOf(star2.getFavorites()).compareTo(String.valueOf(star1.getFavorites()));
                    }else if(property.equalsIgnoreCase("VIEWS")){
                        return String.valueOf(star2.getViews()).compareTo(String.valueOf(star1.getViews()));
                    }
                    return star2.getName().compareTo(star1.getName());
                }
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Products oldItem, Products newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Products item1, Products item2) {
                return false;
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });


    }*/



}
