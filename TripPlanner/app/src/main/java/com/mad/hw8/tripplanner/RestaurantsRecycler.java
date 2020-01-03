package com.mad.hw8.tripplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsRecycler extends RecyclerView.Adapter<RestaurantsRecycler.ViewHolder> {

    Context context;
    AddTrip addTrip;
    ArrayList<RestaurantPlace> list;
    public RestaurantsRecycler(Context context, ArrayList<RestaurantPlace> list,  AddTrip addTrip) {
        this.context = context;
        this.list = list;
        this.addTrip = addTrip;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_restaurants_list_structure, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RestaurantPlace place = list.get(position);
        holder.name.setText(place.placeName);
        holder.rating.setText("Rating: "+place.rating+"/5");
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(place.isSelected);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                place.isSelected =  holder.checkBox.isChecked();
                if(place.isSelected) {
                    addTrip.addPlace(place);
                }else{
                    addTrip.deletePlace(place);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView name, rating;
        public ViewHolder(View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            this.name = itemView.findViewById(R.id.rest_name);
            this.rating = itemView.findViewById(R.id.rating);
        }
    }

    interface AddTrip{
        void addPlace(RestaurantPlace place);
        void deletePlace(RestaurantPlace place);
    }

}
