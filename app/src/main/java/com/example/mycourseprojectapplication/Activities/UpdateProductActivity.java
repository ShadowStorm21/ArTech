package com.example.mycourseprojectapplication.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName(); // used for debugging
    private ProgressBar progressBar;                                       // declare our variables
    private TextInputEditText editTextName,editTextPrice,editTextStock;
    private EditText editTextDescription;
    private String productBrand,productCategory;
    private String[] mobileBrands = {"Apple", "Samsung", "Huawei", "Google","OnePlus","LG","Sony"};
    private String[] tvBrands = {"Samsung","Apple","LG","Sony","TCL"};
    private String[] laptopBrands = {"Apple", "Samsung","Dell","Acer","MSI","LG"};
    private String[] headsetBrands = {"Apple", "Audio-Technica","Bose","Sony","Sennheiser","Beyerdynamic"};
    private String[] smartWatcheBrands = {"Apple", "Samsung","Garmin","Fitbit","Fossil"};
    private String[] cameraBrands = {"Canon", "Nikon","Pentax","Sony","Olympus","Fujifilm"};
    private String[] categories = {"Smartphones","Laptops","TV's","Headsets","Smartwatches","Cameras"};
    private android.widget.AutoCompleteTextView autoCompleteTextView;
    private Products products;
    private ArrayAdapter<String> categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        Toolbar toolbar = findViewById(R.id.toolbarAddProduct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        // setup the toolbar and set title
        setTitle("Update Product");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // when back button is pressed finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        autoCompleteTextView = findViewById(R.id.autoCompleteEditText);
        progressBar = findViewById(R.id.progressBar3);
        categoryAdapter = new ArrayAdapter(this, R.layout.dropdown_list_item, categories);
        android.widget.AutoCompleteTextView categoryAutoCompleteTextView = findViewById(R.id.CategoryAutoCompleteEditText);        // initialize view and setup dropdown list
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);
        editTextName = findViewById(R.id.editTextProductName);
        editTextDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextProductPrice);
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        editTextStock = findViewById(R.id.editTextProductStock);

        products = (Products) getIntent().getSerializableExtra("product");

        if(products != null)
        {
            editTextName.setText(products.getProductName());
            editTextDescription.setText(products.getProductDescription());
            editTextPrice.setText(products.getProductPrice()+"");
            editTextStock.setText(products.getQuantity()+"");
            autoCompleteTextView.setText(products.getProductBrand());
            categoryAutoCompleteTextView.setText(products.getCategory());
            categoryAdapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, categories);
            categoryAutoCompleteTextView.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();
            productBrand = products.getProductBrand();
            productCategory = products.getCategory();
            if(products.getCategory().equals("Smartphones"))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, mobileBrands);            // get product category and based on category change the dropdown values
                autoCompleteTextView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else if(products.getCategory().equals("Laptops"))
            {

                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, laptopBrands);
                autoCompleteTextView.setAdapter(adapter);
            }
            else if(products.getCategory().equals("TV's"))
            {

                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, tvBrands);
                autoCompleteTextView.setAdapter(adapter);
            }
            else if(products.getCategory().equals("Headsets"))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, headsetBrands);
                autoCompleteTextView.setAdapter(adapter);
            }
            else if(products.getCategory().equals("Smartwatches"))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, smartWatcheBrands);
                autoCompleteTextView.setAdapter(adapter);
            }
            else if(products.getCategory().equals("Cameras"))
            {
                ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, cameraBrands);
                autoCompleteTextView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }



            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    productBrand = s.toString();                   // get the product brand

                }
            });



        }

        categoryAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                productCategory = s.toString();
                if(s.toString().equals("Smartphones"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, mobileBrands);            // get product category and based on category change the dropdown values
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Laptops"))
                {

                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, laptopBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("TV's"))
                {

                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, tvBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Headsets"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, headsetBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Smartwatches"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, smartWatcheBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Cameras"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(UpdateProductActivity.this, R.layout.dropdown_list_item, cameraBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }



            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productBrand = s.toString();                   // get the product brand

            }
        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() { // when button add is clicked
            @Override
            public void onClick(View v) {
                if(checkEmptyFields() && !productBrand.isEmpty()) // check for empty fields then upload
                {
                    updateProduct();
                }
            }
        });

    }

    private boolean checkEmptyFields() // method to check for empty fields
    {

        if(editTextName.getText().toString().isEmpty() || editTextDescription.getText().toString().isEmpty() || editTextPrice.getText().toString().isEmpty())
        {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateProduct()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Stock");
        databaseReference.child(products.getProduct_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> values = new HashMap();
                for (DataSnapshot product : snapshot.getChildren()) {
                    values.put(product.getKey(),product.getValue());
                }
                values.put("category",productCategory);
                values.put("productBrand",productBrand);
                values.put("productDescription",editTextDescription.getText().toString());
                values.put("productName",editTextName.getText().toString());
                values.put("productPrice",Double.parseDouble(editTextPrice.getText().toString()));
                values.put("quantity",Integer.parseInt(editTextStock.getText().toString()));
                databaseReference.child(products.getProduct_id()).updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdateProductActivity.this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}