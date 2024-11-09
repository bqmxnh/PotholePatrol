package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class FragmentMap extends Fragment {
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Cấu hình OSMDroid
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        View view = inflater.inflate(R.layout.activity_map, container, false);

        // Khởi tạo MapView
        mapView = view.findViewById(R.id.mapView);
        initializeMap();

        // Xử lý sự kiện zoom
        ImageView btnZoomIn = view.findViewById(R.id.button_zoom_in);
        ImageView btnZoomOut = view.findViewById(R.id.button_zoom_out);

        btnZoomIn.setOnClickListener(v -> {
            mapView.getController().zoomIn();
        });

        btnZoomOut.setOnClickListener(v -> {
            mapView.getController().zoomOut();
        });

        // Xử lý nút định vị
        LinearLayout btnLocation = view.findViewById(R.id.location_button);
        btnLocation.setOnClickListener(v -> {
            // Tọa độ ĐHQG-HCM
            GeoPoint startPoint = new GeoPoint(10.8731, 106.8044);
            mapView.getController().animateTo(startPoint);
        });

        // Sự kiện nhấn cho icon_search
        view.findViewById(R.id.icon_search).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.map_search);

            LinearLayout btnConfirm = bottomSheetDialog.findViewById(R.id.btn_confirm);
            if (btnConfirm != null) {
                btnConfirm.setOnClickListener(confirmView -> {
                    bottomSheetDialog.dismiss();
                    BottomSheetDialog mapListDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
                    mapListDialog.setContentView(R.layout.map_list);
                    mapListDialog.show();
                });
            }
            bottomSheetDialog.show();
        });

        // Sự kiện nhấn cho icon_report
        view.findViewById(R.id.icon_report).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.map_report);

            LinearLayout btnUpload = bottomSheetDialog.findViewById(R.id.btn_upload_image);
            if (btnUpload != null) {
                btnUpload.setOnClickListener(uploadView -> {
                    bottomSheetDialog.dismiss();
                    BottomSheetDialog uploadDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
                    uploadDialog.setContentView(R.layout.map_upload_image);
                    uploadDialog.show();
                });
            }
            bottomSheetDialog.show();
        });

        return view;
    }

    private void initializeMap() {
        // Temporarily set to MAPNIK to test
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Add compass and rotation overlays
        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        // Set default location and zoom (DHQG-HCM)
        GeoPoint startPoint = new GeoPoint(10.8731, 106.8044);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(15.0);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}