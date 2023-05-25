package com.example.a2023appmapapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;


import java.util.Arrays;
import java.util.List;


public class LocationTracker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_location_tracker);

        Button submitButton = findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = getLocation();
                ontoMapsPage(view);
                //Now you can make the API request here.
            }
        });
        String location = getLocation();

    }
    /**
     * What I want is for the user to enter his or her location. Then the API would want to search
     * for nearby hiking locations or nature spots to see the sunset or sunrise.
     */
    public String getLocation()
    {
        TextView t =  findViewById(R.id.editTextTextLocation);
        String returnString = t.getText().toString();
        return returnString;
    }

    /**
     * Transition method into maps and location functionality.
     * @param v
     */
    public void ontoMapsPage(View v)
    {
        String location = getLocation();
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("userLocation", location);
        startActivity(i);
    }
}