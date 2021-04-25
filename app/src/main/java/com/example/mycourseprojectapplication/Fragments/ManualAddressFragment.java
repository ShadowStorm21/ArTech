package com.example.mycourseprojectapplication.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Activities.PaymentActivity;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Services.GeocodingService;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;


public class ManualAddressFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private TextInputEditText editTextStreetAddress, editTextFirstName, editTextLastName, editTextPhoneNumber, editTextCity, editTextState, editTextZIPCode;
    private TextInputLayout textInputLayoutFN,textInputLayoutLN,textInputLayoutSA,textInputLayoutPN,textInputLayoutCity,textInputLayoutState,textInputLayoutZIPCode;
    private String firstName = "",lastName = "",phoneNumber = "";
    private UserSession userSession;                                        // declare our variables
    private SwitchMaterial saveAddressSwitch;
    private Handler mHandler = new Handler();              // create a background handler to retrieve users info from session
    private Handler mHandler1 = new Handler();              // create a background handler to retrieve users info from session
    private static final String PATTERN_SPECIAL_SYM = "[!@#$%&*()_.\\\\+=|<>?{}/~-]";
    private static final String PATTERN_DIGITS = "[0-9]";
    private String city = "",address = "",state = "";
    private Geocoder coder;
    private Address mLocation = null;
    private ProgressBar progressBarLocation;
    private ScrollView scrollView;
    private AddressReceiver mResultReceiver = new AddressReceiver(mHandler1);
    private TextView textViewApproxLoc;
    private LinearLayout linearLayout;

    public ManualAddressFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ConnectionDetector(getContext());// check if user has internet connection or not
        userSession = new UserSession(getContext()); // initialize user session


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manually_address, container, false);
        editTextFirstName = view.findViewById(R.id.editTextFN);
        editTextLastName = view.findViewById(R.id.editTextLN);
        linearLayout = view.findViewById(R.id.addresscontainer);
        editTextStreetAddress = view.findViewById(R.id.editTextStreetAddress);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextState = view.findViewById(R.id.editTextState);
        editTextPhoneNumber = view.findViewById(R.id.editTextPN);         // initialize views and classes
        editTextZIPCode = view.findViewById(R.id.editTextZIPCode);
        textInputLayoutFN = view.findViewById(R.id.textInputLayoutFN);
        textInputLayoutLN = view.findViewById(R.id.textInputLayoutLN);
        textInputLayoutCity = view.findViewById(R.id.textInputLayoutCity);
        textInputLayoutZIPCode = view.findViewById(R.id.textInputLayoutZIPCode);
        textInputLayoutSA = view.findViewById(R.id.textInputLayoutSA);
        textInputLayoutState = view.findViewById(R.id.textInputLayoutState);
        textInputLayoutPN = view.findViewById(R.id.textInputLayoutPN);
        saveAddressSwitch = view.findViewById(R.id.switchSaveAddress);
        scrollView = view.findViewById(R.id.containerScrollView);
        progressBarLocation = view.findViewById(R.id.progressBarLocation);
        textViewApproxLoc = view.findViewById(R.id.textViewApproxLoc);
       // scrollView.setVisibility(View.VISIBLE);
        Button button = view.findViewById(R.id.button);
        coder = new Geocoder(getContext());

        mHandler.postDelayed(runnable, 1000); // after 0.15 sec from entering the activity, retrieve user data
        button.setOnClickListener(new View.OnClickListener() {  // on continue button click
            @Override
            public void onClick(View v) {
                if(fieldChecker() && checkForDigits() && checkForSpecialCharacters()) // check for empty fields first
                {


                    String streetAddress = editTextStreetAddress.getText().toString();
                    String city = editTextCity.getText().toString();
                    String state = editTextState.getText().toString();
                    String firstName = editTextFirstName.getText().toString();
                    String lastName = editTextLastName.getText().toString();             // get the required info
                    String phoneNumber = editTextPhoneNumber.getText().toString();
                    String ZIPCode = editTextZIPCode.getText().toString();                // get the required info after checking empty fields

                    if(checkPhoneNumber()) { // check for valid number

                        Intent intent = new Intent(getContext(), GeocodingService.class);
                        intent.putExtra("Receiver", mResultReceiver);
                        intent.putExtra("Address", streetAddress+city+state);
                        Log.e(TAG, "Starting Service");
                        progressBarLocation.setVisibility(View.VISIBLE);
                        textViewApproxLoc.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.INVISIBLE);
                        getActivity().startService(intent);
                        if(saveAddressSwitch.isChecked())
                        {
                            userSession.setFirstName(firstName);
                            userSession.setLastName(lastName);         // set the values in the user session
                            userSession.setCityName(city);
                            userSession.setState(state);
                            userSession.setStreetAddress(streetAddress);
                            userSession.setZipCode(ZIPCode);
                            userSession.setPhoneNumber(phoneNumber);
                            userSession.setIsSwitchIsOn(true); // set switch to on
                        }
                        else
                        {
                            userSession.setFirstName(null);
                            userSession.setLastName(null);
                            userSession.setCityName(null);           // set session values to null
                            userSession.setState(null);
                            userSession.setStreetAddress(null);
                            userSession.setZipCode(null);
                            userSession.setPhoneNumber(null);
                            userSession.setIsSwitchIsOn(false);
                        }
                    }
                }


            }
        });
        fieldWatcher(); // call method to listen to input data change

        saveAddressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  // on switch changed
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) // if is switched on
                {
                    if(fieldChecker() && checkPhoneNumber() && checkForDigits() && checkForSpecialCharacters()) { // check if fields are not empty and phone number is valid

                        String streetAddress = editTextStreetAddress.getText().toString();
                        String city = editTextCity.getText().toString();
                        String state = editTextState.getText().toString();
                        String firstName = editTextFirstName.getText().toString();
                        String lastName = editTextLastName.getText().toString();             // get the required info
                        String phoneNumber = editTextPhoneNumber.getText().toString();
                        String ZIPCode = editTextZIPCode.getText().toString();
                        userSession.setFirstName(firstName);
                        userSession.setLastName(lastName);         // set the values in the user session
                        userSession.setCityName(city);
                        userSession.setState(state);
                        userSession.setStreetAddress(streetAddress);
                        userSession.setZipCode(ZIPCode);
                        userSession.setPhoneNumber(phoneNumber);
                        userSession.setIsSwitchIsOn(true); // set switch to on
                    }

                }
                else // if it is not on
                {
                    editTextStreetAddress.setText("");
                    editTextCity.setText("");
                    editTextState.setText("");
                    editTextFirstName.setText("");
                    editTextLastName.setText("");
                    editTextPhoneNumber.setText("");
                    editTextZIPCode.setText("");
                    userSession.setFirstName(null);
                    userSession.setLastName(null);
                    userSession.setCityName(null);           // set session values to null
                    userSession.setState(null);
                    userSession.setStreetAddress(null);
                    userSession.setZipCode(null);
                    userSession.setPhoneNumber(null);
                    userSession.setIsSwitchIsOn(false);
                }
            }
        });

        return view;
    }

    private Runnable runnable = new Runnable() {    // create a runnable object to  retrieve user's info in background
        public void run() {

            progressBarLocation.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
            if(userSession.isIsSwitchIsOn())    // check if switch was on
            {
                progressBarLocation.setVisibility(View.GONE);
                editTextFirstName.setText(userSession.getFirstName());
                editTextLastName.setText(userSession.getLastName());
                editTextCity.setText(userSession.getCityName());               // get info and set them in fields
                editTextState.setText(userSession.getState());
                editTextPhoneNumber.setText(userSession.getPhoneNumber());
                editTextStreetAddress.setText(userSession.getStreetAddress());
                editTextZIPCode.setText(userSession.getZipCode());
                saveAddressSwitch.setChecked(true);
                scrollView.setVisibility(View.VISIBLE);
            }
            else
            {
                scrollView.setVisibility(View.VISIBLE);
                progressBarLocation.setVisibility(View.GONE);
                saveAddressSwitch.setChecked(false);
                userSession.setFirstName(null);
                userSession.setLastName(null);
                userSession.setCityName(null);           // set session values to null
                userSession.setState(null);
                userSession.setStreetAddress(null);
                userSession.setZipCode(null);
                userSession.setPhoneNumber(null);
                userSession.setIsSwitchIsOn(false);
            }

        }
    };


    private boolean fieldChecker() // method to check for empty fields
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
        if(editTextCity.getText().toString().trim().isEmpty())
        {
            textInputLayoutCity.setError("City Name Should not be empty");
            return false;
        }
        if(editTextStreetAddress.getText().toString().trim().isEmpty())
        {
            textInputLayoutSA.setError("Street Address Should not be empty");
            return false;
        }
        if(editTextState.getText().toString().trim().isEmpty())
        {
            textInputLayoutState.setError("State Name Should not be empty");
            return false;
        }
        if(editTextZIPCode.getText().toString().trim().isEmpty())
        {
            textInputLayoutZIPCode.setError("ZIP Code Should not be empty");
            return false;
        }
        if(editTextPhoneNumber.getText().toString().trim().isEmpty())
        {
            textInputLayoutPN.setError("Phone Number should not be empty");
            return false;
        }
        textInputLayoutFN.setError("");
        textInputLayoutLN.setError("");
        textInputLayoutCity.setError("");
        textInputLayoutZIPCode.setError("");
        textInputLayoutState.setError("");
        textInputLayoutSA.setError("");
        textInputLayoutPN.setError("");
        return true;
    }

    private void fieldWatcher() // method that listens to input change and get input after change
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

        editTextStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutSA.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                address = s.toString();
            }
        });

        editTextCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutCity.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                city = s.toString();
            }
        });

        editTextZIPCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutZIPCode.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutState.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                state = s.toString();

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

    private boolean checkPhoneNumber() // method to check phone number where length should be less that 8 and should start with 9 or 7
    {
        if(editTextPhoneNumber.getText().toString().trim().length() > 8)
        {
            textInputLayoutPN.setError("Phone Number should not exceed 8 digits");
            return false;
        }
        if(!editTextPhoneNumber.getText().toString().trim().matches("^[7,9]\\d{2}\\d{2}\\d{3}$"))
        {
            textInputLayoutPN.setError("Phone Number should start with 7 or 9");
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        new ConnectionDetector(getContext()); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        mHandler.postDelayed(runnable, 1000); // after 0.15 sec from entering the activity, retrieve user data
       // scrollView.setVisibility(View.VISIBLE);
        //progressBarLocation.setVisibility(View.INVISIBLE);
    }

    class AddressReceiver extends ResultReceiver {

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == 100) {
                final Address address = resultData.getParcelable("Address");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLocation.setVisibility(View.INVISIBLE);
                        textViewApproxLoc.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getContext(), PaymentActivity.class);
                        intent.putExtra("total", getActivity().getIntent().getDoubleExtra("total", 0));
                        intent.putExtra("products", getActivity().getIntent().getSerializableExtra("products"));
                        intent.putExtra("latitude", address.getLatitude());
                        intent.putExtra("longitude", address.getLongitude());
                        intent.putExtra("city", address.getLocality());
                        intent.putExtra("country", address.getCountryName());
                        intent.putExtra("fn", firstName);
                        intent.putExtra("ln", lastName);
                        intent.putExtra("pn", phoneNumber);
                        intent.putExtra("isDirect", getActivity().getIntent().getBooleanExtra("isDirect", false));
                        startActivity(intent);
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.setVisibility(View.VISIBLE);
                        progressBarLocation.setVisibility(View.INVISIBLE);
                        textViewApproxLoc.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), resultData.getString("DATA_KEY")+"", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


}