package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.Interfaces.ItemClickListener;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.MyViewHolder> {

    private final ArrayList<Products> productsArrayList;
    private Context context;                                      // declare our variables
    private ItemClickListener itemClickListener;

    public RelatedProductAdapter(ArrayList<Products> productsArrayList, ItemClickListener itemClickListener) {                  // adapter constructor
        this.productsArrayList = productsArrayList;
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder // view holder describes item view in the recycler view
    {
        public TextView productName,productBrand,productPrice,textViewReviewCount;    // declare our variables
        public ImageView productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewProductNameRecent);
            productBrand = itemView.findViewById(R.id.textViewProductBrandRecent);
            productPrice = itemView.findViewById(R.id.textViewProductPriceRecent);         // initialize our views
            productImage = itemView.findViewById(R.id.productImageRecent);
            textViewReviewCount = itemView.findViewById(R.id.textViewRatingCount);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {       // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_product_item,parent,false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { // bind the data
        Products product = productsArrayList.get(position); // get individual product
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).fitCenter().placeholder(R.drawable.ic_baseline_cached_24).into(holder.productImage); // load the image from the url into product image
        holder.productName.setText(product.getProductName());                                           // set the product details
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(product.getProductPrice());
        holder.productPrice.setText( "$" +price);
        holder.productBrand.setText(product.getProductBrand());


        holder.itemView.setOnClickListener(new View.OnClickListener() {  // on item clicked
            @Override
            public void onClick(View v) {

                itemClickListener.onItemClick(holder,holder.getAdapterPosition(),holder.productImage);  // call the interface method on item click and pass the holder and position and product image
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();  // return the size of array list
    }
}
