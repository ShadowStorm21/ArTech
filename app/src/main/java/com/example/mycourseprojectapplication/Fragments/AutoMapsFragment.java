package com.example.mycourseprojectapplication.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.BuildConfig;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.ConnectionDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.mycourseprojectapplication.Fragments.AutoAddressFragment.setMyAddress;
import static com.example.mycourseprojectapplication.Fragments.AutoAddressFragment.setMyCity;
import static com.example.mycourseprojectapplication.Fragments.AutoAddressFragment.setMyCountry;
import static com.example.mycourseprojectapplication.Fragments.AutoAddressFragment.setMyLocation;

public class AutoMapsFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Double Latitude = 0.0, Longitude = 0.0;
    private LatLng userLocation;
    private Marker place1;
    private Location location;
    private FusedLocationProviderClient fusedLocationClient;  // a location service which combines GPS location and network location
    private Geocoder geocoder; //  help finds the coordinates of a place or address
    private List<Address> addresses;
    private Location newLocation;
    private TextView textViewAddress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initClasses();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_auto_maps, container, false);
        if(checkPermissions())
        {
            getLastLocation();
        }
        else
        {
            requestPermissions();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);

        }
    }

    private void initClasses() // method to initialize geocoder and fused location client
    {
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }


    private void getLastLocation() // Method to get User Location
    {


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        location = task.getResult();
                        if (location == null) // check if the location from the result is null or not
                        {
                            requestNewLocationData(); // if null -> request new Location Data
                        } else {

                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);// Here 3 represent max location result to returned, by documents it recommended 1 to 5

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            try {
                                String address = addresses.get(0).getAddressLine(0); // get the address of the current location using the list of addresses we obtained
                                if(addresses.get(0).getLocality().equals("Sib"))
                                {
                                    setMyCity("Seeb");
                                }
                                else
                                {
                                    setMyCity(addresses.get(0).getLocality());
                                }
                                setMyCountry(addresses.get(0).getCountryName());
                                Latitude = location.getLatitude();
                                Longitude = location.getLongitude();
                                userLocation = new LatLng(Latitude, Longitude); // get user location
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 15f);
                                // Zoom in, animating the camera.
                                mMap.animateCamera(cameraUpdate);
                                setMyLocation(location);
                                if(addresses.get(0).getAddressLine(0).contains("Sib"))
                                {
                                    setMyAddress(addresses.get(0).getAddressLine(0).replace("Sib","Seeb"));
                                    place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0).replace("Sib","Seeb")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                }
                                else
                                {
                                    setMyAddress(addresses.get(0).getAddressLine(0));
                                    place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                }
                                place1.showInfoWindow();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
        );

    }


    private void requestNewLocationData() // Method to request new Location Data
    {
        LocationRequest mLocationRequest = new LocationRequest();  //LocationRequest objects are used to request a quality of service for location updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //priority of the request
        mLocationRequest.setInterval(0);  // Get the desired interval of this request, in milliseconds.
        mLocationRequest.setFastestInterval(0);  // Get the fastest interval of this request, in milliseconds.
        mLocationRequest.setNumUpdates(1);  // Get the number of updates requested
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() // callback after getting requesting Location Updates
    {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            Location mLastLocation = locationResult.getLastLocation(); // get the last location
            location = mLastLocation;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 5); // Here 3 represent max location result to returned, by documents it recommended 1 to 5
                //convert the addresses (postal address) into geo coordinates as latitude and longitude
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                userLocation = new LatLng(Latitude, Longitude); // get user location
                if(addresses.get(0).getLocality().equals("Sib"))
                {
                    setMyCity("Seeb");
                }
                else
                {
                    setMyCity(addresses.get(0).getLocality());

                }
                setMyCountry(addresses.get(0).getCountryName());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 15f);
                // Zoom in, animating the camera.
                mMap.animateCamera(cameraUpdate);
                if(addresses.get(0).getAddressLine(0).contains("Sib"))
                {
                    setMyAddress(addresses.get(0).getAddressLine(0).replace("Sib","Seeb"));
                    place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0).replace("Sib","Seeb")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                else
                {
                    setMyAddress(addresses.get(0).getAddressLine(0));
                    place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                place1.showInfoWindow();


            } catch (IOException e) {
                e.printStackTrace();
            }
            setMyLocation(location);

        }
    };


    private void enableMyLocation() {

        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            mMap.setMyLocationEnabled(true);
            }

    }
    private void requestPermissions() // Method to request permission to get the user Location
    {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        mMap.clear();
        newLocation = mMap.getMyLocation();
        try
        {
            addresses = geocoder.getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 5); // Here 3 represent max location result to returned, by documents it recommended 1 to 5
            //convert the addresses (postal address) into geo coordinates as latitude and longitude
            if(addresses.get(0).getLocality().equals("Sib"))
            {
                setMyCity("Seeb");
            }
            else
            {
                setMyCity(addresses.get(0).getLocality());
            }
            setMyCountry(addresses.get(0).getCountryName());
            Latitude = newLocation.getLatitude();
            Longitude = newLocation.getLongitude();
            userLocation = new LatLng(Latitude, Longitude); // get user location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation,15f);
            // Zoom in, animating the camera.
            mMap.animateCamera(cameraUpdate);
            if(addresses.get(0).getAddressLine(0).contains("Sib"))
            {
                setMyAddress(addresses.get(0).getAddressLine(0).replace("Sib","Seeb"));
                place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0).replace("Sib","Seeb")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else
            {
                setMyAddress(addresses.get(0).getAddressLine(0));
                place1 = mMap.addMarker(new MarkerOptions().position(userLocation).title(addresses.get(0).getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            place1.showInfoWindow();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        setMyLocation(newLocation);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Your Current Location", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        new ConnectionDetector(getContext()); // on Resume ( this method is fired before on create )  activity, check if user has connection or not
        if(!checkPermissions())
        {
            requestPermissions();
        }
        else
        {
            requestNewLocationData();
            enableMyLocation();
            getLastLocation();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        googleMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Your Current Address",
                Toast.LENGTH_SHORT).show();
    }
    private boolean checkPermissions()  // Method to check if user gave the permission to access his Location or not
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) // check if request code is the same
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission

                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // if user clicked deny permanently
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))); // go to application settings
                    Toast.makeText(getContext(), "Enable location!", Toast.LENGTH_LONG).show();
                } else {
                    requestPermissions();
                }


            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // permission granted

                requestNewLocationData(); // get location data
                getLastLocation(); // get last known location
            }
        }
    }
}