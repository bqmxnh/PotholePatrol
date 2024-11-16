package com.example.potholepatrol.UICustom;

import android.view.View;

import com.example.potholepatrol.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class ViewOverlayInfoWindow extends InfoWindow {
    public ViewOverlayInfoWindow(View view, MapView mapView) {
        super(R.layout.map_pothole_click, mapView);
        mView = view;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        // Thiết lập vị trí cho view
        mView.setX((float) marker.getPosition().getLatitude());
        mView.setY((float) marker.getPosition().getLongitude());
        mView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClose() {
        mView.setVisibility(View.GONE);
    }
}
