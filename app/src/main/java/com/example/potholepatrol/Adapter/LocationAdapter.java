package com.example.potholepatrol.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.potholepatrol.Fragment.LocationFragment.NominatimResult;
import com.example.potholepatrol.R;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<NominatimResult> {
    private Context context;
    private List<NominatimResult> locations;

    public LocationAdapter(Context context, List<NominatimResult> locations) {
        super(context, 0, locations);
        this.context = context;
        this.locations = locations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
        }

        NominatimResult currentLocation = locations.get(position);

        TextView tvLocationName = listItem.findViewById(R.id.tvLocationName);
        TextView tvLocationAddress = listItem.findViewById(R.id.tvLocationAddress);
        ImageView ivGoIcon = listItem.findViewById(R.id.ivGoIcon);

        String[] parts = currentLocation.getDisplay_name().split(",", 2);
        String name = parts[0].trim();
        String address = parts.length > 1 ? parts[1].trim() : "";

        tvLocationName.setText(name);
        tvLocationAddress.setText(address);

        return listItem;
    }
}