package com.example.mycourseprojectapplication.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mycourseprojectapplication.Models.Products;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddProductActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName(); // used for debugging
    private ViewPager mViewPager;
    private ArrayList<Uri> ImageList = new ArrayList();
    private ArrayList<String> urlStrings;
    private static final int PICK_IMAGE = 1;
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
    private Chip chipColor1,chipColor2,chipColor3,chipColor4,chipColor5,chipColor6,chipConfig1,chipConfig2,chipConfig3;
    private String colorSelected ="", configurationSelected= "";
    private HashMap<String,String> colorMap = new HashMap<>();
    private HorizontalScrollView horizontalScrollViewConfig,horizontalScrollViewColor;
    private static final String PATTERN_SPECIAL_SYM = "[!@#$%&*()_.\\\\+=|<>?{}/~-]";
    private static final String PATTERN_DIGITS = "[0-9]";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbarAddProduct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        // setup the toolbar and set title
        setTitle("Add Product");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // when back button is pressed finish the activity
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ExtendedFloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        autoCompleteTextView = findViewById(R.id.autoCompleteEditText);
        progressBar = findViewById(R.id.progressBar3);
        mViewPager = findViewById(R.id.viewPagerAdmin);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter(this, R.layout.dropdown_list_item, categories);
        android.widget.AutoCompleteTextView categoryAutoCompleteTextView = findViewById(R.id.CategoryAutoCompleteEditText);        // initialize view and setup dropdown list
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);
        editTextName = findViewById(R.id.editTextProductName);
        editTextDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextProductPrice);
        Button button = findViewById(R.id.buttonAdd);
        editTextStock = findViewById(R.id.editTextProductStock);
        chipColor1 = findViewById(R.id.chipAdminColor1);
        chipColor2 = findViewById(R.id.chipAdminColor2);
        chipColor3 = findViewById(R.id.chipAdminColor3);
        chipColor4 = findViewById(R.id.chipAdminColor4);
        chipColor5 = findViewById(R.id.chipAdminColor5);
        chipColor6 = findViewById(R.id.chipAdminColor6);
        chipConfig1 = findViewById(R.id.chipAdminConfig1);
        chipConfig2 = findViewById(R.id.chipAdminConfig2);
        chipConfig3 = findViewById(R.id.chipAdminConfig3);
        horizontalScrollViewColor = findViewById(R.id.horiColorLayout);
        horizontalScrollViewConfig = findViewById(R.id.horiConfigLayout);
        floatingActionButton.setOnClickListener(new View.OnClickListener() { // on plus sign click
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");                                         // open a window to select multiple product images
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        categoryAutoCompleteTextView.setText("Smartphones",false); // set dropdown list default value
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
                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, mobileBrands);            // get product category and based on category change the dropdown values
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Laptops"))
                {

                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, laptopBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("TV's"))
                {

                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, tvBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Headsets"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, headsetBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Smartwatches"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, smartWatcheBrands);
                    autoCompleteTextView.setAdapter(adapter);
                }
                else if(s.toString().equals("Cameras"))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter(AddProductActivity.this, R.layout.dropdown_list_item, cameraBrands);
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
                productBrand = s.toString();// get the product brand

               /* switch (productCategory) { // change the config based on category

                    case "Smartphones":
                        chipConfig1.setText("128GB");
                        chipConfig2.setText("256GB");
                        chipConfig3.setText("512GB");
                        break;
                    case "Laptops":

                        switch (productBrand)
                        {
                            case "Apple":
                                chipConfig1.setText("Intel Core i5 + 8GB Ram + 256GB SSD");
                                chipConfig2.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                                chipConfig3.setText("Apple M1 + 16GB Ram + 1TB SDD");
                                break;
                            case "Samsung":
                                chipConfig1.setText("Intel Core i3 + 8GB Ram + 256GB SSD");
                                chipConfig2.setText("Intel Core i5 + 8GB Ram + 512GB SSD");
                                chipConfig3.setText("Intel Core i7 + 8GB Ram + 1TB SSD");
                                break;
                            case "MSI" :
                                chipConfig1.setText("Intel Core i7 + 16GB Ram + 1TB HDD + 512GB SSD + RTX 2070");
                                chipConfig2.setText("Intel Core i9 + 32GB Ram + 1TB HDD + 1TB SSD + RTX 3070");
                                chipConfig3.setText("Intel Core i9 + 64GB Ram + 1TB HDD + 1TB SSD + RTX 3080");
                                break;
                            case "Acer":
                                chipConfig1.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                                chipConfig2.setText("Intel Core i7 + 32GB Ram + 1TB SSD");
                                chipConfig3.setText("Intel Core i9 + 32GB Ram + 2TB SSD");
                                break;
                            case "Dell":
                                chipConfig1.setText("Intel Core i7 + 16GB Ram + 512GB SSD");
                                chipConfig2.setText("Intel Core i7 + 16GB Ram + 1TB SSD");
                                chipConfig3.setText("Intel Core i7 + 32GB Ram + 1TB SDD");
                                break;
                            case "LG" :
                                chipConfig1.setText("Intel Core i3 + 8GB Ram + 512GB SSD");
                                chipConfig2.setText("Intel Core i5 + 16GB Ram + 1TB SSD");
                                chipConfig3.setText("Intel Core i7 + 32GB Ram + 1TB SDD");
                                break;
                            default:
                                chipConfig1.setText("Intel Core i3 + 16GB Ram + 512GB SSD");
                                chipConfig2.setText("Intel Core i5 + 16GB Ram + 1TB SSD");
                                chipConfig3.setText("Intel Core i7 + 32GB Ram + 1TB SDD");
                                break;

                        }

                        break;
                    case "TV's":
                        horizontalScrollViewColor.setVisibility(View.GONE);
                        horizontalScrollViewConfig.setVisibility(View.GONE);
                        colorSelected = "Default";
                        configurationSelected = "Default";
                        break;
                    case "Headsets":
                        horizontalScrollViewConfig.setVisibility(View.GONE);
                        horizontalScrollViewColor.setVisibility(View.VISIBLE);
                        configurationSelected = "Default";
                        break;
                    case "Smartwatches":

                         horizontalScrollViewConfig.setVisibility(View.GONE);
                         horizontalScrollViewColor.setVisibility(View.VISIBLE);
                         configurationSelected = "Default";
                        break;
                    case "Cameras":
                        horizontalScrollViewColor.setVisibility(View.GONE);
                        horizontalScrollViewConfig.setVisibility(View.GONE);
                        colorSelected = "Default";
                        configurationSelected = "Default";
                        break;

                }

            }*/
            }
        });


        button.setOnClickListener(new View.OnClickListener() { // when button add is clicked
            @Override
            public void onClick(View v) {
                if(checkEmptyFields() && !productBrand.isEmpty() && checkForSpecialCharacters()) // check for empty fields then upload
                upload();
            }
        });







    }

    public class myViewPagerAdapter extends PagerAdapter { // create a view pager for images

        private ArrayList<Uri> uriArrayList; // create array of uri

        public myViewPagerAdapter(ArrayList<Uri> uriArrayList) {
            this.uriArrayList = uriArrayList;            // set the uri array list
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View)object;
            container.removeView(view);     // get the view then destroy it
            view = null;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.findViewById(R.id.imageView3);                      // initialize our imageview inside the viewpager
            imageView.setImageURI(uriArrayList.get(position)); // set image view uri to arraylist with position to each image
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // set the scale to center crop
            container.addView(imageView); // add image to view pager container
            return imageView; // return the image view
        }

        @Override
        public int getCount() {
            return uriArrayList.size();
        } // return the size or uri

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==((ImageView)object);          // return the object which is the image view
        }
    }

    private boolean checkEmptyFields() // method to check for empty fields
    {

        if(editTextName.getText().toString().trim().isEmpty() || editTextDescription.getText().toString().trim().isEmpty() || editTextPrice.getText().toString().trim().isEmpty() || editTextStock.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean checkForSpecialCharacters()
    {
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM, Pattern.CASE_INSENSITIVE);
        if(patternSym.matcher(editTextName.getText().toString().trim()).find())
        {
            Toast.makeText(this, "Product name should not have special characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(patternSym.matcher(editTextDescription.getText().toString().trim()).find())
        {
            Toast.makeText(this, "Product description should not have special characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void upload() { // method to upload images to the cloud firestorage

        if(ImageList.size() < 2)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "You must choose images first!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        urlStrings = new ArrayList<>();
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("photos"); // create a storage reference
        int upload_count = 0;
        for (upload_count = 0; upload_count < ImageList.size(); upload_count++) { // iterate over the image list


            Uri IndividualImage = (Uri) ImageList.get(upload_count); // get each image

            //  This will create dynamic image view and add them to ViewFlipper


            final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());// create a reference to each image

            ImageName.putFile(IndividualImage).addOnSuccessListener( // upload image to firebase
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener( // get download url for each image after a successful upload
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlStrings.add(String.valueOf(uri)); // store urls to a different array list


                                            if (urlStrings.size() == ImageList.size()) { // check if the size of url string is the same as the image list
                                                storeLink(urlStrings); // store the url links as a map
                                            }

                                        }
                                    }
                            );
                        }
                    }
            );


        }


    }

    private void storeLink(ArrayList urlStrings) { // method to return a hashmap of key , value pairs since firebase doesn't accept arrays

        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < urlStrings.size(); i++) {
            hashMap.put("ImgLink" + i, (String) urlStrings.get(i));
        }



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Stock");
        String product_id = UUID.randomUUID().toString(); // create product id
        String productName = editTextName.getText().toString();
        String productDescription = editTextDescription.getText().toString(); // get the product details
        double productPrice = Double.parseDouble(editTextPrice.getText().toString()); // get the price
        int quantity = Integer.parseInt(editTextStock.getText().toString());

        Products product = new Products(product_id,productName,productDescription,productBrand,productPrice,hashMap,System.currentTimeMillis(),productCategory,quantity); // create a product object
        String key = databaseReference.push().getKey();


        if(key != null) { // check if the key is not null
            databaseReference.child(product_id).setValue(product) // add the product to the database
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {

                                    Toast.makeText(AddProductActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show(); // show success message on complete
                                    editTextName.setText("");
                                    editTextDescription.setText("");
                                    editTextPrice.setText("");
                                    editTextStock.setText("");
                                    progressBar.setVisibility(View.GONE); // hide progress bar
                                    }

                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show(); // show error on failure
                    progressBar.setVisibility(View.GONE); // hide progress bar
                }
            });
        }

        ImageList.clear(); // clear image list after finishing
        mViewPager.setAdapter(null); // set viewpager adapter to null
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // on activity result from getting the images
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) { // check if request code is the same
            if (resultCode == RESULT_OK) { // check if the results are ok


                if (data.getClipData() != null) { // check if the data is not null

                    int countClipData = data.getClipData().getItemCount(); // get the number of data available (images)
                    int currentImageSelected = 0;

                    while (currentImageSelected < countClipData) {

                        Uri imageUri = data.getClipData().getItemAt(currentImageSelected).getUri();  // get the uri from each image
                        ImageList.add(imageUri); // add uri to the list
                        AddProductActivity.myViewPagerAdapter myViewPagerAdapter = new myViewPagerAdapter(ImageList); // set image list as the array list for view pager
                        mViewPager.setAdapter(myViewPagerAdapter); // set the adapter for view pager
                        currentImageSelected++; // increase counter
                    }


                } else {
                    Toast.makeText(AddProductActivity.this, "Please Select Multiple Images ( 2 or more)", Toast.LENGTH_SHORT).show(); //  if user selected 1 image show error message
                }

            }

        }

    }
}