package com.mad.hw8.tripplanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        setTitle("View Map");
        if (getIntent() != null) {
            trip = (Trip) getIntent().getSerializableExtra("Trip");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        if (trip.places.size()==1) {
            RestaurantPlace place = trip.places.get(0);
            LatLng src = new LatLng(place.lat, place.lng);
            mMap.addMarker(new MarkerOptions().position(src).title(place.placeName));
            boundsBuilder.include(src);
            final LatLngBounds latLngBounds = boundsBuilder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
                }
            });
        } else {
            for (int i = 0; i < trip.places.size() - 1; i++) {
                RestaurantPlace place = trip.places.get(i);
                RestaurantPlace pr = trip.places.get(i + 1);
                LatLng src = new LatLng(place.lat, place.lng);
                LatLng dest = new LatLng(pr.lat, pr.lng);
                mMap.addMarker(new MarkerOptions().position(src).title(place.placeName));
                mMap.addMarker(new MarkerOptions().position(dest).title(pr.placeName));
                boundsBuilder.include(src);
                boundsBuilder.include(dest);
                mMap.addPolyline(
                        new PolylineOptions().add(
                                new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude)
                        ).width(2).color(Color.BLUE).geodesic(true)
                );
            }
            final LatLngBounds latLngBounds = boundsBuilder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));
                }
            });
        }

    }
}
