package com.example.mycourseprojectapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.Activities.UpdateProductActivity;
import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminAllProductAdapter extends RecyclerView.Adapter<AdminAllProductAdapter.MyViewHolder> {
    private final ArrayList<Products> productsArrayList;
    private Context context;

    public AdminAllProductAdapter(ArrayList<Products> productsArrayList) {
        this.productsArrayList = productsArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_all_product_item,parent,false);
        context = view.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Products product = productsArrayList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productCategory.setText(product.getCategory());
        Glide.with(context).load(product.getProductImages().get("ImgLink0")).centerCrop().placeholder(R.drawable.ic_baseline_cached_24).into(holder.imageViewProductImage); // load the image from the url into product image
        holder.imageViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateProductActivity.class);
                intent.putExtra("product",product);
                context.startActivity(intent);
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)                           // create an alert before deleting the product
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to delete this product? all product details will be gone!")
                        .setCancelable(true)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
                                databaseReference.child(product.getProduct_id()).removeValue(); // remove the cart item from the database
                                productsArrayList.remove(position); // remove products
                                notifyDataSetChanged(); // notify of data changes to adapter
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView productName,productCategory;
        public ImageView imageViewUpdate,imageViewProductImage;
        public Button buttonDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textViewAdminAllProductTitle);
            productCategory = itemView.findViewById(R.id.textViewAdminCategory);
            imageViewUpdate = itemView.findViewById(R.id.imageViewUpdateProduct);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteProduct);
            imageViewProductImage = itemView.findViewById(R.id.imageView21);
        }
    }
}
