package com.example.mycourseprojectapplication.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mycourseprojectapplication.Models.Orders;
import com.example.mycourseprojectapplication.Models.Tracking;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment {

    private Orders orders;
    private LatLng userLoc;
    private LatLng raiderLoc;
    private HashMap<String, Marker> mMarkers = new HashMap<>();           // declare our variables
    private GoogleMap mMap;
    String product_id;
    HashMap<String,String> trackingKey;
    Tracking tracking;
    private Location currLocation,prevLocation;
    private TextView textViewDistance,textViewTime;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {       // on map initialized

        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;

            try {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Tracking");

                        myRef.child(tracking.getTracking_key()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (Map.Entry<String, String> entry : trackingKey.entrySet()) {

                                    if (entry.getValue().equals(tracking.getTracking_key()) && product_id.equals(entry.getKey())) {
                                        double userLat = Double.parseDouble(snapshot.child("userLatitude").getValue().toString());
                                        double userLong = Double.parseDouble(snapshot.child("userLongitude").getValue().toString());
                                        userLoc = new LatLng(userLat, userLong);

                                        setMarker(snapshot);
                                        Log.i("Maps", snapshot.toString());

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






            }catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        orders = (Orders) getActivity().getIntent().getSerializableExtra("order");
        trackingKey = (HashMap<String, String>) getActivity().getIntent().getSerializableExtra("tracking_key");
        tracking = (Tracking) getActivity().getIntent().getSerializableExtra("tracking");
        product_id = getActivity().getIntent().getStringExtra("product_id");
        textViewDistance = view.findViewById(R.id.textViewDistance);
        textViewTime = view.findViewById(R.id.textViewTime);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


   private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        mMap.clear();
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double raiderLat =  Double.parseDouble(value.get("raiderLatitude").toString());
        double raiderLong =  Double.parseDouble(value.get("raiderLongitude").toString());
        raiderLoc = new LatLng(raiderLat,raiderLong);
        mMarkers.put(key, mMap.addMarker(new MarkerOptions().title("Raider Location").position(raiderLoc)));
        mMarkers.put("user",mMap.addMarker(new MarkerOptions().title("User Location").position(userLoc)));
        mMap.resetMinMaxZoomPreference();
       CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(raiderLoc,15f);
       // Zoom in, animating the camera.
       getDistanceAndTime();
       mMap.animateCamera(cameraUpdate);
        mMarkers.clear();
        value.clear();
    }
    private void getDistanceAndTime()
    {


        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
            DistanceMatrix matrixApiRequest = req.origins(userLoc.latitude+","+userLoc.longitude)
                    .destinations(raiderLoc.latitude+","+raiderLoc.longitude)
                    .mode(TravelMode.DRIVING)
                    .avoid(DirectionsApi.RouteRestriction.HIGHWAYS)
                    .language("en-EN")
                    .await();
            String distance = matrixApiRequest.rows[0].elements[0].distance.humanReadable;
            String time = matrixApiRequest.rows[0].elements[0].duration.humanReadable;
            textViewDistance.setText(distance);
            textViewTime.setText("Within "+time);
            if(distance.equals("1 m"))
            {
                textViewTime.setText("Your raider has arrived!");
            }

            //Do something with result here
            // ....
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

    }





}