package com.example.potholepatrol.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.example.potholepatrol.Activity.LoginActivity;
import com.example.potholepatrol.PotholeDetails.Pothole;
import com.example.potholepatrol.R;
import com.example.potholepatrol.UICustom.ViewOverlayInfoWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private String accessToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);

        // Get access token from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", "");

        if (accessToken.isEmpty()) {
            Log.e(TAG, "Access token not found");
            Toast.makeText(requireContext(), "Authentication error. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return rootView;
        }

        // Initialize osmdroid configuration
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        requestPermissions();

        mapView = rootView.findViewById(R.id.mapView);
        setupMap();
        setupLocation();
        setupMapControls(rootView);

        return rootView;
    }

    private void setupMapControls(View rootView) {
        ImageView fabLocation = rootView.findViewById(R.id.icon_location);
        ImageView buttonZoomIn = rootView.findViewById(R.id.button_zoom_in);
        ImageView buttonZoomOut = rootView.findViewById(R.id.button_zoom_out);

        fabLocation.setOnClickListener(v -> centerMapOnMyLocation());
        buttonZoomIn.setOnClickListener(v -> {
            if (mapView.getZoomLevelDouble() < mapView.getMaxZoomLevel()) {
                mapView.getController().zoomIn();
            }
        });
        buttonZoomOut.setOnClickListener(v -> {
            if (mapView.getZoomLevelDouble() > mapView.getMinZoomLevel()) {
                mapView.getController().zoomOut();
            }
        });
    }

    private void setupMap() {
        final double MIN_LAT = 10.8593387269177;
        final double MIN_LON = 106.734790771361;
        final double MAX_LAT = 10.89728831078;
        final double MAX_LON = 106.8587615275;

        XYTileSource tileSource = new XYTileSource(
                "CustomMBTiles",
                8, 18,
                256,
                ".png",
                new String[]{ BASE_URL + "/map/tiles/" }
        ) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                int zoom = MapTileIndex.getZoom(pMapTileIndex);
                int x = MapTileIndex.getX(pMapTileIndex);
                int y = MapTileIndex.getY(pMapTileIndex);
                int tmsY = (int) (Math.pow(2, zoom) - 1 - y);
                if (isTileInBounds(x, y, zoom)) {
                    String url = BASE_URL + "/map/tiles/" + zoom + "/" + x + "/" + tmsY + ".png";
                    return url + "?Authorization=Bearer " + accessToken;
                }
                return null;
            }
        };

        Configuration.getInstance().getAdditionalHttpRequestProperties().put(
                "Authorization",
                "Bearer " + accessToken
        );

        mapView.setTileSource(tileSource);
        mapView.setMultiTouchControls(true);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);

        BoundingBox bounds = new BoundingBox(
                MAX_LAT, MAX_LON,
                MIN_LAT, MIN_LON
        );
        mapView.setScrollableAreaLimitDouble(bounds);

        mapView.getController().setZoom(15.0);
        GeoPoint center = new GeoPoint(
                (MIN_LAT + MAX_LAT) / 2,
                (MIN_LON + MAX_LON) / 2
        );
        mapView.getController().setCenter(center);

        loadPotholes();
    }

    private boolean isTileInBounds(int x, int y, int zoom) {
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);

        return !(east < 106.734790771361 ||
                west > 106.8587615275 ||
                south > 10.89728831078 ||
                north < 10.8593387269177);
    }

    private static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

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

    private void loadPotholes() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + "/map/getAllPothole")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error loading potholes", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to load pothole data", Toast.LENGTH_SHORT).show()
                );
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
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error processing pothole data", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else if (response.code() == 401) {
                    Log.e(TAG, "Unauthorized access");
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                    });
                }
            }
        });
    }

    private void displayPotholes(List<Pothole> potholes) {
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker || overlay instanceof Polyline);

        if (potholes.size() >= 2) {
            Pothole startPoint = potholes.get(0);
            Pothole endPoint = potholes.get(1);

            drawRouteBetweenPoints(startPoint, endPoint);

            Marker startMarker = createMarker(startPoint, R.drawable.marker_red);
            Marker endMarker = createMarker(endPoint, R.drawable.marker_red);

            mapView.getOverlays().add(startMarker);
            mapView.getOverlays().add(endMarker);
        }

        mapView.invalidate();
    }


    private Marker createMarker(Pothole pothole, int iconRes) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(
                pothole.getLocation().getCoordinates().getLatitude(),
                pothole.getLocation().getCoordinates().getLongitude()
        ));

        Drawable icon = ContextCompat.getDrawable(requireContext(), iconRes);
        marker.setIcon(icon);

        marker.setOnMarkerClickListener((m, map) -> {
            showPotholeDetails(pothole, marker);
            return true;
        });

        return marker;
    }

    private void drawRouteBetweenPoints(Pothole start, Pothole end) {
        String url = String.format(Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=polyline",
                start.getLocation().getCoordinates().getLongitude(),
                start.getLocation().getCoordinates().getLatitude(),
                end.getLocation().getCoordinates().getLongitude(),
                end.getLocation().getCoordinates().getLatitude()
        );

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> drawDirectLine(start, end));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            String geometry = route.getString("geometry");
                            double distance = route.getDouble("distance");
                            double duration = route.getDouble("duration");

                            List<GeoPoint> points = decodePolyline(geometry);

                            requireActivity().runOnUiThread(() -> {
                                Polyline routeLine = new Polyline(mapView);
                                routeLine.setColor(Color.BLUE);
                                routeLine.setWidth(10f);
                                routeLine.setPoints(points);
                                mapView.getOverlays().add(routeLine);
                                mapView.invalidate();

                                String routeInfo = String.format(Locale.US,
                                        "Distance: %.1f km\nEstimated time: %.0f min",
                                        distance / 1000.0, duration / 60.0);
                                Toast.makeText(requireContext(), routeInfo, Toast.LENGTH_LONG).show();
                            });
                        }
                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() -> drawDirectLine(start, end));
                    }
                } else {
                    requireActivity().runOnUiThread(() -> drawDirectLine(start, end));
                }
            }
        });
    }
    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> points = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            points.add(new GeoPoint(lat / 1E5, lng / 1E5));
        }
        return points;
    }

    private void drawDirectLine(Pothole start, Pothole end) {
        GeoPoint startPoint = new GeoPoint(
                start.getLocation().getCoordinates().getLatitude(),
                start.getLocation().getCoordinates().getLongitude()
        );

        GeoPoint endPoint = new GeoPoint(
                end.getLocation().getCoordinates().getLatitude(),
                end.getLocation().getCoordinates().getLongitude()
        );

        Polyline line = new Polyline(mapView);
        line.setColor(Color.BLUE);
        line.setWidth(10f);

        List<GeoPoint> points = new ArrayList<>();
        points.add(startPoint);
        points.add(endPoint);
        line.setPoints(points);

        mapView.getOverlays().add(line);
        mapView.invalidate();

        float[] results = new float[1];
        Location.distanceBetween(
                startPoint.getLatitude(), startPoint.getLongitude(),
                endPoint.getLatitude(), endPoint.getLongitude(),
                results
        );

        String distance = String.format(Locale.US, "Distance: %.1f m", results[0]);
        Toast.makeText(requireContext(), distance, Toast.LENGTH_LONG).show();
    }



    private void showPotholeDetails(Pothole pothole, Marker marker) {
        for (Object overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker) {
                ((Marker) overlay).closeInfoWindow();
            }
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.map_pothole_click, null);

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

        ViewOverlayInfoWindow infoWindow = new ViewOverlayInfoWindow(dialogView, mapView);
        marker.setInfoWindow(infoWindow);
        marker.setInfoWindowAnchor(0.5f, -1f);
        marker.showInfoWindow();

        closeIcon.setOnClickListener(v -> marker.closeInfoWindow());
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
                    this
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
                Toast.makeText(requireContext(), "Finding your location...", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestPermissions();
        }
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
                Toast.makeText(requireContext(), "One or more permissions denied. Please check app settings.", Toast.LENGTH_SHORT).show();
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
        locationManager.removeUpdates(this);
    }
}