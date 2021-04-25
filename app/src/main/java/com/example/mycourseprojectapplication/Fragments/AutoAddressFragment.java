package com.example.mycourseprojectapplication.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Activities.AddressActivity;
import com.example.mycourseprojectapplication.Activities.PaymentActivity;
import com.example.mycourseprojectapplication.BuildConfig;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;


public class AutoAddressFragment extends Fragment{

    private final String TAG = AutoAddressFragment.this.getClass().getSimpleName();  // this is used for debugging
    private TextInputEditText editTextFirstName,editTextLastName,editTextPhoneNumber;        // declare our variables
    private String city = "", country = "";
    private TextInputLayout textInputLayoutFN,textInputLayoutLN,textInputLayoutPN;
    private String firstName = "",lastName = "",phoneNumber = "";
    public static Location myLocation;
    public static String myAddress;
    public static String myCity;
    public static String myCountry;
    public static boolean isRationale = false;
    private static final String PATTERN_SPECIAL_SYM = "[!@#$%&*()_.\\\\+=|<>?{}/~-]";
    private static final String PATTERN_DIGITS = "[0-9]";
    private static final String PATTERN_OMAN= "^[7,9]\\d{2}\\d{2}\\d{3}$";


    public static Location getMyLocation() {
        return myLocation;
    }

    public static void setMyLocation(Location myLocation) {
        AutoAddressFragment.myLocation = myLocation;
    }


    public static String getMyAddress() {
        return myAddress;
    }

    public static void setMyAddress(String myAddress) {
        AutoAddressFragment.myAddress = myAddress;
    }

    public static String getMyCity() {
        return myCity;
    }

    public static void setMyCity(String myCity) {
        AutoAddressFragment.myCity = myCity;
    }

    public static String getMyCountry() {
        return myCountry;
    }

    public static void setMyCountry(String myCountry) {
        AutoAddressFragment.myCountry = myCountry;
    }

    public static boolean isIsRationale() {
        return isRationale;
    }

    public static void setIsRationale(boolean isRationale) {
        AutoAddressFragment.isRationale = isRationale;
    }

    public AutoAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_auto_address, container, false);
        editTextFirstName = view.findViewById(R.id.editTextFirstName);                         // initialize the views
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        textInputLayoutFN = view.findViewById(R.id.textInputLayoutFirstName);
        textInputLayoutLN = view.findViewById(R.id.textInputLayoutLastName);
        textInputLayoutPN = view.findViewById(R.id.textInputLayoutPhoneNumber);
        Button buttonNext = view.findViewById(R.id.buttonNext);
        new ConnectionDetector(getContext()); // check if user has internet connection or not
        fieldWatcher();     // call method listens to data change
        buttonNext.setOnClickListener(new View.OnClickListener() {     // on continue button click
            @Override
            public void onClick(View v) {

                if(fieldChecker()) // check for empty fields
                {

                    if(getMyLocation() != null) {     // check if location not null

                        if (checkForSpecialCharacters() && checkForDigits()) { // check for special characters

                            if (checkPhoneNumber()) {      // check if number starts with 9 or 7

                                Intent intent = new Intent(getContext(), PaymentActivity.class);
                                intent.putExtra("total", getActivity().getIntent().getDoubleExtra("total", 0));
                                intent.putExtra("products", getActivity().getIntent().getSerializableExtra("products"));                   // go to payment activity and bundle required info
                                intent.putExtra("latitude", myLocation.getLatitude());
                                intent.putExtra("longitude", myLocation.getLongitude());
                                intent.putExtra("city", getMyCity());
                                intent.putExtra("country", getMyCountry());
                                intent.putExtra("fn", editTextFirstName.getText().toString());
                                intent.putExtra("ln", editTextLastName.getText().toString());
                                intent.putExtra("pn", editTextPhoneNumber.getText().toString());
                                intent.putExtra("isDirect", getActivity().getIntent().getBooleanExtra("isDirect", false));
                                startActivity(intent);
                            }
                        }
                    }
                }

            }
        });

        return view;
    }

    private boolean fieldChecker()   // method to check for empty fields
    {
        if(editTextFirstName.getText().toString().trim().isEmpty())
        {
            textInputLayoutFN.setError("First Name Should not be empty");
            return false;
        }
        if(editTextLastName.getText().toString().trim().isEmpty())
        {
            textInputLayoutLN.setError("Last Name Should not be empty");
            return false;
        }
        if(editTextPhoneNumber.getText().toString().trim().isEmpty())
        {
            textInputLayoutPN.setError("Phone Number should not be empty");
            return false;
        }
        textInputLayoutFN.setError("");
        textInputLayoutLN.setError("");
        textInputLayoutPN.setError("");
        return true;
    }
    private boolean checkForSpecialCharacters()
    {
        Pattern patternSym = Pattern.compile(PATTERN_SPECIAL_SYM, Pattern.CASE_INSENSITIVE);
        if(patternSym.matcher(editTextFirstName.getText().toString().trim()).find())
        {
            textInputLayoutFN.setError("First name Should not contain special characters!");
            return false;
        }
        if(patternSym.matcher(editTextLastName.getText().toString().trim()).find())
        {
            textInputLayoutLN.setError("Last name should not contain special characters!");
            return false;
        }
        if(patternSym.matcher(editTextPhoneNumber.getText().toString().trim()).find()) {
            textInputLayoutPN.setError("Phone number should not contain special characters!");
            return false;
        }
        textInputLayoutFN.setError("");
        textInputLayoutLN.setError("");
        textInputLayoutPN.setError("");
        return true;
    }
    private boolean checkForDigits()
    {
        Pattern patternSym = Pattern.compile(PATTERN_DIGITS);
        if(patternSym.matcher(editTextFirstName.getText().toString().trim()).find())
        {
            textInputLayoutFN.setError("First name Should not contain digits!");
            return false;
        }
        if(patternSym.matcher(editTextLastName.getText().toString().trim()).find())
        {
            textInputLayoutLN.setError("Last name should not contain digits!");
            return false;
        }
        textInputLayoutFN.setError("");
        textInputLayoutLN.setError("");
        return true;
    }

    private void fieldWatcher() // method that get the input data
    {
        editTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                textInputLayoutFN.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

                firstName = s.toString();
            }
        });

        editTextLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutLN.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                lastName = s.toString();
            }
        });

        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutPN.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

                phoneNumber = s.toString();

            }
        });
    }

    private boolean checkPhoneNumber() // method that check phone number not more than 8 digits and must start with 9 or 7
    {
        Pattern patternOman = Pattern.compile(PATTERN_OMAN);
        if(editTextPhoneNumber.getText().toString().trim().length() > textInputLayoutPN.getCounterMaxLength())
        {
            textInputLayoutPN.setError("Phone Number should not exceed digit limit!");
            return false;
        }

        if (patternOman.matcher(editTextPhoneNumber.getText().toString().trim()).find())
        {
            return true;
        }
        else
         {
             textInputLayoutPN.setError("Phone Number should format is wrong!");
             return false;
         }

    }



    @Override
    public void onResume() {
        super.onResume();
        new ConnectionDetector(getContext()); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        if(!checkPermissions())
        {
            requestPermissions();
        }
        if(!isLocationEnabled())
        {
            Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void requestPermissions() // Method to request permission to get the user Location
    {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length == 0)
        {
            requestPermissions();
            return;
        }
        if(grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)
        {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // if user clicked deny permanently
                ((AddressActivity) getActivity()).goToFirstPage();
                Snackbar.make(getView(),"This feature is disabled due to location permission denied", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))); // go to application settings
                    }
                }).show();


            }
            else
            {
                requestPermissions();
                return;
            }


        }

    }

    private boolean checkPermissions()  // Method to check if user gave the permission to access his Location or not
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() // check if the location on the phone is Enabled or not
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


}