package com.example.potholepatrol.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.PotholeDetails.Pothole;
import com.example.potholepatrol.R;
import com.example.potholepatrol.UICustom.ViewOverlayInfoWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentMap extends Fragment implements LocationListener {

    private static final String TAG = "FragmentMap";
    private static final String BASE_URL = "http://47.129.31.47:3000";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3MzE3NDQ1ODEsImV4cCI6MTczMTgzMDk4MSwiYXVkIjoiNjcyNWQzZTNmMGZiM2NmZDg2MDA4Y2Y3IiwiaXNzIjoiMjI1MjAxMjBAZ20udWl0LmVkdS52biJ9.l_P0sohNnVmTk2oaG_ttG3PYYxlMFsKe5VV6JsRLziw";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);

        // Initialize osmdroid configuration
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        // Request permissions
        requestPermissions();

        // Initialize map
        mapView = rootView.findViewById(R.id.mapView);
        setupMap();
        setupLocation();

        ImageView fabLocation = rootView.findViewById(R.id.icon_location);
        fabLocation.setOnClickListener(v -> centerMapOnMyLocation());

        return rootView;
    }


    private void setupMap() {
        // Định nghĩa bounds cho ĐHQG-HCM
        final double MIN_LAT = 10.8593387269177;
        final double MIN_LON = 106.734790771361;
        final double MAX_LAT = 10.89728831078;
        final double MAX_LON = 106.8587615275;

        // Setup custom tile source với bounds checking
        XYTileSource tileSource = new XYTileSource(
                "CustomMBTiles",
                8, 18, // min/max zoom
                256, // tile size
                ".png",
                new String[]{ BASE_URL + "/map/tiles/" }
        ) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                int zoom = MapTileIndex.getZoom(pMapTileIndex);
                int x = MapTileIndex.getX(pMapTileIndex);
                int y = MapTileIndex.getY(pMapTileIndex);
                int tmsY = (int) (Math.pow(2, zoom) - 1 - y);
                // Kiểm tra xem tile có nằm trong khu vực không
                if (isTileInBounds(x, y, zoom)) {
                    String url = BASE_URL + "/map/tiles/" + zoom + "/" + x + "/" + tmsY + ".png";
                    Log.d(TAG, "Requesting tile in bounds: " + url);
                    return url + "?Authorization=Bearer " + token;
                } else {
                    Log.d(TAG, "Tile out of bounds: z=" + zoom + " x=" + x + " y=" + y);
                    return null;
                }
            }
        };

        Configuration.getInstance().getAdditionalHttpRequestProperties().put(
                "Authorization",
                "Bearer " + token
        );

        // Setup map view
        mapView.setTileSource(tileSource);
        mapView.setMultiTouchControls(true);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);

        // Set bounds giới hạn
        BoundingBox bounds = new BoundingBox(
                MAX_LAT, MAX_LON,  // North, East
                MIN_LAT, MIN_LON   // South, West
        );
        mapView.setScrollableAreaLimitDouble(bounds);

        // Set zoom và center
        mapView.getController().setZoom(15.0);
        GeoPoint center = new GeoPoint(
                (MIN_LAT + MAX_LAT) / 2,
                (MIN_LON + MAX_LON) / 2
        );
        mapView.getController().setCenter(center);

        loadPotholes();
    }
    // Helper method để kiểm tra tile có nằm trong bounds không
    private boolean isTileInBounds(int x, int y, int zoom) {
        // Convert tile coordinates to lat/lon
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);

        // Check overlap với bounds của ĐHQG
        return !(east < 106.734790771361 || // west bound
                west > 106.8587615275 ||    // east bound
                south > 10.89728831078 ||   // north bound
                north < 10.8593387269177);  // south bound
    }
    // Convert tile coordinates to latitude
    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
    // Convert tile coordinates to longitude
    private static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    private void setupLocation() {
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        if (checkLocationPermission()) {
            startLocationUpdates();
        }
    }


    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void startLocationUpdates() {
        if (checkLocationPermission()) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10,
                    location -> {
                        GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mapView.getController().animateTo(myPosition);
                    }
            );
        }
    }


    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean shouldRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                shouldRequest = true;
                break;
            }
        }

        if (shouldRequest) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
        } else {
            setupLocation();
        }
    }



    private void centerMapOnMyLocation() {
        if (checkLocationPermission()) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                GeoPoint myPosition = new GeoPoint(
                        lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()
                );
                mapView.getController().animateTo(myPosition);
            } else {
                Toast.makeText(requireContext(), "Đang tìm vị trí của bạn...", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestPermissions();
        }
    }

    private void loadPotholes() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + "/map/getAllPothole")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error loading potholes", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Pothole>>() {}.getType();
                        List<Pothole> potholes = gson.fromJson(json, type);

                        requireActivity().runOnUiThread(() -> displayPotholes(potholes));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing pothole data", e);
                    }
                }
            }
        });
    }

    private void displayPotholes(List<Pothole> potholes) {
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        for (Pothole pothole : potholes) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(
                    pothole.getLocation().getCoordinates().getLatitude(),
                    pothole.getLocation().getCoordinates().getLongitude()
            ));

            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.marker_red);
            marker.setIcon(icon);

            marker.setOnMarkerClickListener((m, map) -> {
                showPotholeDetails(pothole, marker); // Hiển thị layout tùy chỉnh
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();
    }

    private void showPotholeDetails(Pothole pothole, Marker marker) {
        // Đóng tất cả InfoWindow hiện tại
        for (Object overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker) {
                ((Marker) overlay).closeInfoWindow();
            }
        }

        // Inflate layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.map_pothole_click, null);

        // Tìm các view và gán dữ liệu
        TextView titleAddress = dialogView.findViewById(R.id.title_address);
        TextView titleAddressDetails = dialogView.findViewById(R.id.title_address_details);
        ImageView closeIcon = dialogView.findViewById(R.id.ic_close);

        titleAddress.setText("Pothole Information");
        titleAddressDetails.setText(String.format(
                "Severity: %s\nDimension: %s\nDepth: %s",
                pothole.getSeverity().getLevel(),
                pothole.getDescription().getDimension(),
                pothole.getDescription().getDepth()
        ));

        // Tạo InfoWindow mới
        ViewOverlayInfoWindow infoWindow = new ViewOverlayInfoWindow(dialogView, mapView);

        // Thiết lập InfoWindow cho marker
        marker.setInfoWindow(infoWindow);

        // **Đặt offset để đẩy InfoWindow lên trên marker**
        marker.setInfoWindowAnchor(0.5f, -1f); // Điều chỉnh giá trị này theo ý muốn

        // Hiển thị InfoWindow
        marker.showInfoWindow();

        // Thiết lập sự kiện cho nút close
        closeIcon.setOnClickListener(v -> marker.closeInfoWindow());
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().animateTo(myPosition);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                setupLocation();
            } else {
                Toast.makeText(requireContext(), "Một hoặc nhiều quyền không được cấp, vui lòng kiểm tra lại cài đặt ứng dụng.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (checkLocationPermission()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locationManager.removeUpdates(location -> {});
    }
}
