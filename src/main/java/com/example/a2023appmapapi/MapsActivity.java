package com.example.a2023appmapapi;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.a2023appmapapi.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SearchView mapSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Places api
         */
        Places.initialize(getApplicationContext(), "AIzaSyDPa7NbEmhvu9jfDgqboHrVg08Fg7gTgUs");
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapSearchView = findViewById(R.id.mapSearch);


        /**
         * From previous page this is the user inputted zip code.
         */
        String initialQuery = getIntent().getStringExtra("userLocation");
        mapSearchView.setQuery(initialQuery + " park", false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //When user submits something into the search bar.
                String zipCode = mapSearchView.getQuery().toString();
                displayNearestParks(zipCode);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }


    private void displayNearestParks(String zipCode) {
        // Create a Geocoder instance
        Geocoder geocoder = new Geocoder(this);

        // Perform geocoding to get the location coordinates
        try {
            List<Address> addressList = geocoder.getFromLocationName(zipCode, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng zipCodeLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Clear existing markers on the map
                mMap.clear();

                // Add a marker for the zip code location
                mMap.addMarker(new MarkerOptions().position(zipCodeLatLng).title(zipCode));

                // Move the camera to the zip code location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zipCodeLatLng, 10));

                // Search for nearby parks using the Places API
                PlacesClient placesClient = Places.createClient(this);
                FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Task<FindCurrentPlaceResponse> currentPlaceTask = placesClient.findCurrentPlace(request);

                currentPlaceTask.addOnSuccessListener(response -> {
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Place place = placeLikelihood.getPlace();
                        LatLng parkLatLng = place.getLatLng();
                        String parkName = place.getName();

                        // Add a marker for each nearby park
                        mMap.addMarker(new MarkerOptions().position(parkLatLng).title(parkName));
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(MapsActivity.this, "Failed to retrieve nearby parks.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
            } else {
                Toast.makeText(MapsActivity.this, "Location not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(MapsActivity.this, "Geocoding failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    @Override
    /**
     * Literally this method will just build the map into the app.
     */
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        String initialQuery = mapSearchView.getQuery().toString();

        // Perform geocoding to get the location coordinates
        List<Address> addressList = null;
        if (initialQuery != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(initialQuery, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(initialQuery));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            } else {
                Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
