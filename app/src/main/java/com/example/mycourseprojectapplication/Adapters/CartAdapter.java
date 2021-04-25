package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.Models.Cart;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.mycourseprojectapplication.Activities.CartActivity.mCartItemCount;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private final ArrayList<Cart> productsArrayList;// declare our variables
    private int quantity = 1;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Cart");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();




    public CartAdapter(ArrayList<Cart> productsArrayList) {
        this.productsArrayList = productsArrayList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {    // create item view for the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);
        context = view.getContext();
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) { // bind the data

        final Products product = productsArrayList.get(position).getProduct(); // get products in cart
        final Cart cartItem = productsArrayList.get(position); // get cart item which contains more info such as configuration and color
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).fitCenter().placeholder(R.drawable.ic_baseline_cached_24).into(holder.productImage);  // load the image from the url into product image
        holder.productName.setText(product.getProductName());
        DecimalFormat decimalFormat = new DecimalFormat("0.#####");
        String price = decimalFormat.format(product.getProductPrice());
        holder.productPrice.setText( "$" +price);
        holder.productBrand.setText(product.getProductBrand());                                               // set the products details
        holder.textViewConfig.setText("Configuration : "+cartItem.getConfigurationSelected());
        holder.textViewColor.setText("Color : "+cartItem.getColorSelected());
        ViewCompat.setTransitionName(holder.productImage, product.getProductName());        // set the transition name for the image for (Image transition purpose)
        holder.textViewQuantity.setText(String.valueOf(cartItem.getQuantity()));  // set quantity of items available in cart





        holder.buttonRemove.setOnClickListener(new View.OnClickListener() { // on click button remove
            @Override
            public void onClick(View v) {

                myRef.child(mAuth.getCurrentUser().getUid()).child(product.getProduct_id()).removeValue(); // remove the cart item from the database
                productsArrayList.remove(position); // remove products
                notifyItemRemoved(position); // notify of data changes to adapter
                SharedPreferences sharedPreferences = context.getSharedPreferences("cart",MODE_PRIVATE); // create a shared preferences object to edit the cart value in the user session
                sharedPreferences.edit().putInt("cartValue",mCartItemCount--).commit();

            }
        });

        holder.buttonIncreaseQuantity.setOnClickListener(new View.OnClickListener() { // on click increase quantity button
            @Override
            public void onClick(View v) {
                quantity = cartItem.getQuantity();
                if(quantity < product.getQuantity())
                {
                    quantity++; // increase value
                }
                else
                {
                    Toast.makeText(context, "Your reached the maximum quantity for this item!", Toast.LENGTH_SHORT).show();
                }
                holder.textViewQuantity.setText(String.valueOf(quantity)); // set the value of quantity in textView
                myRef.child(mAuth.getCurrentUser().getUid()).child(product.getProduct_id()).child("quantity").setValue(quantity); // update the quantity of product in the database
                new UserSession(context).increaseCartValue(); // increase the quantity of cart in user session
            }
        });

        holder.buttonDecreaseQuantity.setOnClickListener(new View.OnClickListener() { // on click decrease quantity button
            @Override
            public void onClick(View v) {
                quantity = cartItem.getQuantity();
                if(quantity == 1)
                {
                    quantity--;
                    myRef.child(mAuth.getCurrentUser().getUid()).child(product.getProduct_id()).removeValue(); // remove the cart item from the database
                    productsArrayList.remove(position); // remove products
                    notifyItemRemoved(position); // notify of data changes to adapter
                    SharedPreferences sharedPreferences = context.getSharedPreferences("cart",MODE_PRIVATE); // create a shared preferences object to edit the cart value in the user session
                    sharedPreferences.edit().putInt("cartValue",mCartItemCount--).commit();
                }
                else {
                    quantity--;
                    myRef.child(mAuth.getCurrentUser().getUid()).child(product.getProduct_id()).child("quantity").setValue(quantity); // update the quantity of product in the database
                    holder.textViewQuantity.setText(String.valueOf(quantity));  // set the value of quantity in textView
                    new UserSession(context).decreaseCartValue(); // decrease the quantity of cart in user session
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size(); // return the array list size
    }

    public class CartViewHolder extends RecyclerView.ViewHolder  // view holder describes item view in the recycler view
    {
        public TextView productName,productBrand,productPrice,textViewQuantity,textViewColor,textViewConfig;
        public ImageView productImage;         // declare our variables for views
        public Button buttonRemove,buttonIncreaseQuantity,buttonDecreaseQuantity;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewProductNameRecent);
            productBrand = itemView.findViewById(R.id.textViewProductBrandRecent);
            productPrice = itemView.findViewById(R.id.textViewProductPriceRecent);         // initialize our views
            productImage = itemView.findViewById(R.id.productImageRecent);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
            buttonDecreaseQuantity = itemView.findViewById(R.id.buttonDecrease);
            buttonIncreaseQuantity = itemView.findViewById(R.id.buttonIncrease);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewColor = itemView.findViewById(R.id.textViewColor);
            textViewConfig = itemView.findViewById(R.id.textViewConfig);

        }
    }



}
