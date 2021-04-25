package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mycourseprojectapplication.Activities.OrderStatusActivity;
import com.example.mycourseprojectapplication.Activities.ReviewActivity;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    private final ArrayList<Cart> productsArrayList;        // declare our variables
    private Context context;
    private final Orders order;
    private final ArrayList<Tracking> tracking;

    public OrderProductAdapter(ArrayList<Cart> productsArrayList, Orders order, ArrayList<Tracking> tracking) {
        this.productsArrayList = productsArrayList;
        this.order = order;
        this.tracking = tracking;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  // view holder describes item view in the recycler view
    {
        public TextView textViewProductName,textViewProductBrand,textViewProductPrice,textViewProductConfig,textViewColor,textViewQuantity;
        public Button buttonReview,buttonTrack;               // declare our variables for views
        public ImageView productImage;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductBrand = itemView.findViewById(R.id.textViewProductBrand);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);          // initialize our views
            textViewProductConfig = itemView.findViewById(R.id.textViewProductConfig);
            textViewColor = itemView.findViewById(R.id.textViewColor);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonReview = itemView.findViewById(R.id.buttonReview);
            buttonTrack = itemView.findViewById(R.id.buttonTrack);
            productImage = itemView.findViewById(R.id.imageView9);
            progressBar = itemView.findViewById(R.id.progressBarLoadOrderImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_order_item,parent,false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // bind the data
        Products product = productsArrayList.get(position).getProduct(); // get the products of the order
        Cart cart = productsArrayList.get(position);  // get the color selected and config from cart object
        holder.textViewProductName.setText(product.getProductName());
        holder.textViewProductBrand.setText(product.getProductBrand());        // set the order details and product details
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(cart.getTotalPrice());
        holder.textViewProductPrice.setText("$"+price);
        holder.textViewProductConfig.setText(cart.getConfigurationSelected());
        holder.textViewQuantity.setText(String.valueOf(cart.getQuantity()));
        holder.textViewColor.setText(cart.getColorSelected());
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
        }).into(holder.productImage);  // load the image from the url into product image
        holder.buttonReview.setOnClickListener(new View.OnClickListener() { // on review button is clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("product_id",product.getProduct_id());    // go to review activity and bundle the product id with it
                context.startActivity(intent);
            }
        });
        holder.buttonTrack.setOnClickListener(new View.OnClickListener() { // when track button is clicked
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderStatusActivity.class);
                intent.putExtra("product_id",product.getProduct_id());               // go to order status activity
                Log.i("Adapter",product.getProduct_id());
                intent.putExtra("tracking_keys",order.getTracking_key());
                intent.putExtra("order",order);
                intent.putExtra("tracking",tracking.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size(); // return the size of product array list
    }
}
