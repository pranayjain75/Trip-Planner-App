package com.mad.hw8.tripplanner;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class RestaurantPlace implements Serializable{
    String placeName, placeId;
    double lat;
    double lng;
    double rating;
    boolean isSelected = false;

    public RestaurantPlace(String placeName, String placeId, double lat, double lng, double rating) {
        this.placeName = placeName;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
    }

    public RestaurantPlace() {
    }
}
