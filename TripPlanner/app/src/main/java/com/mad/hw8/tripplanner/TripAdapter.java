package com.mad.hw8.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Context context;
    ArrayList<Trip> list;
    GoogleSignInAccount account;
    CreateMaps createMaps;

    public TripAdapter(Context context, ArrayList<Trip> list, GoogleSignInAccount account, CreateMaps createMaps) {
        this.context = context;
        this.list = list;
        this.account = account;
        this. createMaps = createMaps;
    }


    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_trip_structure, parent, false);
        TripAdapter.ViewHolder viewHolder = new TripAdapter.ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position) {
        final Trip trip = list.get(position);
        holder.name.setText(trip.name);
        holder.city.setText(trip.city);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User/"+account.getId()+"/Trips");
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("User/"+account.getId()+"/Trips");
                myRef.child(trip.id).removeValue();
            }
        });

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMaps.createMapPath(trip);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name, city;
        ImageView map, delete;
        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.trip_name);
            this.city = itemView.findViewById(R.id.city);
            this.map = itemView.findViewById(R.id.map);
            this.delete = itemView.findViewById(R.id.delete);
        }
    }

    interface CreateMaps{
        void createMapPath(Trip trip);
    }
}
