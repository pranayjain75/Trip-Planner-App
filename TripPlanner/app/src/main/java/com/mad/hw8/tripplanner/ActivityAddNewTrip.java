package com.mad.hw8.tripplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ActivityAddNewTrip extends AppCompatActivity implements RestaurantsRecycler.AddTrip {
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView nameTxt, cityTxt;
    Button add, cancel, viewTrip;
    GoogleApiClient mGoogleApiClient;
    ArrayList<RestaurantPlace> restList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<RestaurantPlace> selectedRestList = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference myRef;
    GoogleSignInAccount account;
    //TextView rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        setTitle("Create Trip");
        if(getIntent()!=null) {
            account = getIntent().getParcelableExtra("account");
            nameTxt = findViewById(R.id.name_txt);
            cityTxt = findViewById(R.id.city_txt);
            add = findViewById(R.id.addtoTripBtn);
            cancel = findViewById(R.id.cancelBtn);
            viewTrip = findViewById(R.id.viewTripBtn);
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("User/"+account.getId()+"/Trips");
            cityTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAutocompleteActivity();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nameTxt.getText().toString().isEmpty() || nameTxt.getText()==null || nameTxt.getText().toString()==""){
                        Toast.makeText(ActivityAddNewTrip.this, "Enter Name.", Toast.LENGTH_SHORT).show();
                    }else if(cityTxt.getText().toString().isEmpty()){
                        Toast.makeText(ActivityAddNewTrip.this, "Select City.", Toast.LENGTH_SHORT).show();
                    }else if(selectedRestList.size() == 0){
                        Toast.makeText(ActivityAddNewTrip.this, "Select Restaurants.", Toast.LENGTH_SHORT).show();
                    }else if(selectedRestList.size() > 15) {
                        Toast.makeText(ActivityAddNewTrip.this, "Select upto 15 Restaurants.", Toast.LENGTH_SHORT).show();
                    }else{
                        String id = myRef.push().getKey();
                        Trip trip = new Trip(id, nameTxt.getText().toString(), cityTxt.getText().toString(), selectedRestList);
                        myRef.child(id).setValue(trip);
                        finish();
                        Intent intent = new Intent(ActivityAddNewTrip.this, TripActivity.class);
                        intent.putExtra("account", account);
                        startActivity(intent);
                    }
                }
            });
            recyclerView = findViewById(R.id.restaurants_list);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            viewTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nameTxt.getText().toString().isEmpty() || nameTxt.getText()==null || nameTxt.getText().toString()==""){
                        Toast.makeText(ActivityAddNewTrip.this, "Enter Name.", Toast.LENGTH_SHORT).show();
                    }else if(cityTxt.getText().toString().isEmpty()){
                        Toast.makeText(ActivityAddNewTrip.this, "Select City.", Toast.LENGTH_SHORT).show();
                    }else if(selectedRestList.size() == 0){
                        Toast.makeText(ActivityAddNewTrip.this, "Select Restaurants.", Toast.LENGTH_SHORT).show();
                    }else if(selectedRestList.size() > 15) {
                        Toast.makeText(ActivityAddNewTrip.this, "Select upto 15 Restaurants.", Toast.LENGTH_SHORT).show();
                    }else{
                        Trip trip = new Trip(nameTxt.getText().toString(), cityTxt.getText().toString(), selectedRestList);
                        Intent intent = new Intent(ActivityAddNewTrip.this, MapActivity.class);
                        intent.putExtra("Trip", trip);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void getLocation(Place place) {

        String locationQueryStringUrl = GoogleApiUrl.BASE_URL + GoogleApiUrl.NEARBY_SEARCH_TAG + "/" +
                GoogleApiUrl.JSON_FORMAT_TAG + "?" + GoogleApiUrl.LOCATION_TAG + "=" +
                place.getLatLng().latitude + "," + place.getLatLng().longitude + "&" + GoogleApiUrl.RADIUS_TAG + "=" +
                GoogleApiUrl.RADIUS_VALUE + "&" + GoogleApiUrl.PLACE_TYPE_TAG + "=restaurant" +
                "&" + GoogleApiUrl.API_KEY_TAG + "=" + getResources().getString(R.string.google_api);

        Log.d("demo", locationQueryStringUrl);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(locationQueryStringUrl)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    JSONArray result = root.getJSONArray("results");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject singlePlaceJsonObject = (JSONObject) result.get(i);
                        String currentPlaceName = singlePlaceJsonObject.getString("name");
                        String placeId = singlePlaceJsonObject.getString("place_id");
                        String lat = singlePlaceJsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String lng = singlePlaceJsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        double rate = Double.parseDouble(singlePlaceJsonObject.getString("rating"));
                        RestaurantPlace restaurantPlace = new RestaurantPlace(currentPlaceName, placeId, Double.parseDouble(lat),Double.parseDouble(lng) , rate);
                        restList.add(restaurantPlace);
                    }
                    GoToAdapter();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    private void GoToAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new RestaurantsRecycler(getBaseContext(), restList, ActivityAddNewTrip.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d("Demo", "Place: " + place.getName());
                cityTxt.setText(place.getName());
                getLocation(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Demo", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void openAutocompleteActivity() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("US")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_item:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void addPlace(RestaurantPlace place) {
        selectedRestList.add(place);
    }
    @Override
    public void deletePlace(RestaurantPlace place) {
        selectedRestList.remove(place);
    }
}
