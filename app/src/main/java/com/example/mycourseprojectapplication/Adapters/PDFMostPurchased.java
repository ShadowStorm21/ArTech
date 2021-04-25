package com.example.mycourseprojectapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;

import java.util.List;
import java.util.Map;

public class PDFMostPurchased extends ArrayAdapter {

    private List<Products> orders;
    private Context context;
    private Map<String,Integer> stringIntegerMap;
    private List<Long> integers;

    public PDFMostPurchased(@NonNull Context context, int resource, List<Products> ProductsList, Map<String,Integer> s) {
        super(context, resource, ProductsList);
        this.context = context;
        this.orders = ProductsList;
        this.stringIntegerMap = s;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_pdf_most_purchased_products, null);

        Products product = orders.get(position);


        TextView textViewPId,textViewPname,textViewCategory,textViewTimePurchased;

        textViewPId = view.findViewById(R.id.textViewPID);
        textViewPname = view.findViewById(R.id.textViewPname);
        textViewCategory = view.findViewById(R.id.textViewPcategory);
        textViewTimePurchased = view.findViewById(R.id.textViewTimePurchased);
        for(Map.Entry<String,Integer> entry : stringIntegerMap.entrySet())
        {
            if(entry.getKey().equals(product.getProduct_id()))
            {
                textViewTimePurchased.setText(String.valueOf(entry.getValue()));
            }
        }
        textViewPId.setText(product.getProduct_id().substring(0,13));
        textViewPname.setText(product.getProductName());
        textViewCategory.setText(product.getCategory());


        return  view;
    }
}
