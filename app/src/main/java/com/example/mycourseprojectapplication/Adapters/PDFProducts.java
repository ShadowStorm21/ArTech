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

import java.text.SimpleDateFormat;
import java.util.List;

public class PDFProducts extends ArrayAdapter {

    private List<Products> productsArrayList;
    private Context context;

    public PDFProducts(@NonNull Context context, int resource, List<Products> products) {
        super(context, resource, products);
        this.context = context;
        this.productsArrayList = products;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_pdf_products, null);

        Products product = productsArrayList.get(position);


        TextView textViewPId,textViewPname,textViewCategory,textViewBrand, textViewPrice,textViewQuantity,textViewDate;

        textViewPId = view.findViewById(R.id.textViewPID);
        textViewPname = view.findViewById(R.id.textViewPname);
        textViewCategory = view.findViewById(R.id.textViewPcategory);
        textViewBrand = view.findViewById(R.id.textViewPDFproductBrand);
        textViewDate = view.findViewById(R.id.textViewPDFpDate);
        textViewPrice = view.findViewById(R.id.textViewPDFpPrice);
        textViewQuantity = view.findViewById(R.id.textViewPDFPquantity);
        String date = new SimpleDateFormat("dd MMMM yyyy").format(product.getTimeCreated());
        textViewBrand.setText(product.getProductBrand());
        textViewDate.setText(date);
        textViewPrice.setText("$"+product.getProductPrice());
        textViewQuantity.setText(String.valueOf(product.getQuantity()));
        textViewPId.setText(product.getProduct_id().substring(0,13));
        textViewPname.setText(product.getProductName());
        textViewCategory.setText(product.getCategory());


        return  view;
    }
}
