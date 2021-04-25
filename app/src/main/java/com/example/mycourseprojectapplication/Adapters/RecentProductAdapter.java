package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecentProductAdapter extends RecyclerView.Adapter<RecentProductAdapter.MyViewHolder> {
    private final ArrayList<Products> productsArrayList;
    private Context context;
    private ItemClickListener onItemClick;
    private ArrayList<Rating> ratingArrayList = new ArrayList<>();
    private double total;
    private double avg;
    private double rate;
    public RecentProductAdapter(ArrayList<Products> productsArrayList) {
        this.productsArrayList = productsArrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView productName,productBrand,productPrice;
        public ImageView productImage;
        public RatingBar ratingBar;
        public ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.textViewProductNameRecent);
            productBrand = itemView.findViewById(R.id.textViewProductBrandRecent);
            productPrice = itemView.findViewById(R.id.textViewProductPriceRecent);
            productImage = itemView.findViewById(R.id.productImageRecent);
            ratingBar =  itemView.findViewById(R.id.ratingBar);
            progressBar = itemView.findViewById(R.id.progressBarLoadImageRecent);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item_product,parent,false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Products product = productsArrayList.get(position);
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.productImage.setVisibility(View.VISIBLE);
                return false;
            }
        }).into(holder.productImage);
        holder.productName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(product.getProductPrice());
        holder.productPrice.setText( "$"+price);
        holder.productBrand.setText(product.getProductBrand());
        ViewCompat.setTransitionName(holder.productImage, product.getProductName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClick.onItemClick(holder,holder.getAdapterPosition(),holder.productImage);

            }
        });


    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }


    public void setOnItemClick(ItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }
}
