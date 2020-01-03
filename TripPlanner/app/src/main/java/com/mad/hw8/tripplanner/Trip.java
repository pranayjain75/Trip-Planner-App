package com.mad.hw8.tripplanner;

import java.io.Serializable;
import java.util.ArrayList;

public class Trip implements Serializable {
    String name, city, id;
    ArrayList<RestaurantPlace> places;

    public Trip(String id, String name, String city, ArrayList<RestaurantPlace> places) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.places = places;
    }

    public Trip() {
    }

    public Trip(String name, String city, ArrayList<RestaurantPlace> places) {
        this.name = name;
        this.city = city;
        this.places = places;
    }
}
