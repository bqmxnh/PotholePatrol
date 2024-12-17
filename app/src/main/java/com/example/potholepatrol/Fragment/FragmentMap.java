package com.example.potholepatrol.Fragment;

import static androidx.core.content.ContextCompat.getSystemService;

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
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potholepatrol.Activity.AddPotholeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.Activity.LoginActivity;
import com.example.potholepatrol.Activity.RegisterActivity;
import com.example.potholepatrol.PotholeDetails.Pothole;
import com.example.potholepatrol.R;
import com.example.potholepatrol.UICustom.ViewOverlayInfoWindow;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
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
    private ImageView searchLocation;
    private ImageView reportPothole;
    private Marker myLocationMarker;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private String accessToken;
    private Button btnStartNavigation;
    private Handler handler = new Handler();
    private boolean isRouteUpdating = false;
    private boolean isNavigating = false;
    private List<Pothole> potholes = new ArrayList<>();

    private Button button_exit;
    final double MIN_LAT = 10.8593387269177;
    final double MAX_LAT = 10.89728831078;
    final double MIN_LON = 106.734790771361;
    final double MAX_LON = 106.8587615275;
    private List<Overlay> previousOverlays = new ArrayList<>();
    private Polyline currentRouteLine;
    private Marker currentMarker = null;
    private double currentLat = 0.0;
    private double currentLon = 0.0;

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
        searchLocation = rootView.findViewById(R.id.icon_search);
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocationFragment locationFragment = new LocationFragment();


                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, locationFragment) // Use the correct container ID
                        .addToBackStack(null)
                        .commit();
            }
        });







        // Initialize the new navigation button
        btnStartNavigation = rootView.findViewById(R.id.btn_start_navigation);
        btnStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNavigating) {
                    // Start navigation
                    isNavigating = true;
                    isRouteUpdating = true;
                    btnStartNavigation.setText("Stop");

                    // Start updating the route by posting the updateRouteRunnable to the handler
                    handler.post(updateRouteRunnable);
                    Log.d("Navigation", "Starting navigation...");

                    // Optional: Log or update anything else when starting the navigation
                    Log.d("negative", "Button start navigation clicked");

                } else {
                    // Stop navigation
                    isNavigating = false;
                    btnStartNavigation.setText("Navigate");

                    Log.d("stop", "Button stop navigation clicked");

                    stopUpdatingRoute();
                    SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
                    String latString = preferences.getString("lat", null);
                    String lonString = preferences.getString("lon", null);


                    if (latString != null && lonString != null) {
                        try {
                            // Chuyển đổi từ String sang double
                            double savedLat = Double.parseDouble(latString);
                            double savedLon = Double.parseDouble(lonString);
                            GeoPoint savedGeoPoint = new GeoPoint(savedLat, savedLon);
                            Marker savedLocationMarker = createMarker(savedGeoPoint, R.drawable.marker_blue);

                            mapView.getOverlays().add(savedLocationMarker);

                            // Vẽ tuyến đường giữa vị trí hiện tại và vị trí đã lưu
                            drawRouteBetweenPoints(currentLat, currentLon, savedLat, savedLon);

                            // Cập nhật bản đồ
                            mapView.invalidate();
                        } catch (NumberFormatException e) {
                            // Thông báo lỗi nếu dữ liệu trong SharedPreferences không hợp lệ
                            Toast.makeText(getContext(), "Invalid saved location data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Thông báo nếu không có dữ liệu lat/lon trong SharedPreferences
                        Toast.makeText(getContext(), "No saved location data", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        reportPothole = rootView.findViewById(R.id.icon_report);
        reportPothole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo Intent để chuyển sang AddPotholeActivity
                Intent intent = new Intent(getActivity(), AddPotholeActivity.class);
                startActivity(intent);
            }
        });


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
        XYTileSource tileSource = new XYTileSource(
                "CustomMBTiles",
                8, 18,
                256,
                ".png",
                new String[]{BASE_URL + "/map/tiles/"}
        ) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                int zoom = MapTileIndex.getZoom(pMapTileIndex);
                int x = MapTileIndex.getX(pMapTileIndex);
                int y = MapTileIndex.getY(pMapTileIndex);
                int tmsY = (int) (Math.pow(2, zoom) - 1 - y);
                if (isTileInBounds(x, y, zoom)) {
                    return BASE_URL + "/map/tiles/" + zoom + "/" + x + "/" + tmsY + ".png"
                            + "?Authorization=Bearer " + accessToken;
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
        centerMapOnMyLocation();
        // Gọi hàm cập nhật vị trí theo thời gian thực
        startRealTimeLocationUpdates();

        // Tải dữ liệu khác (ví dụ: potholes)
        loadPotholes();
        addMapClickListener();
    }
    private void addMapClickListener() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Xử lý sự kiện khi người dùng click vào bản đồ
                Toast.makeText(requireContext(), "Clicked at: " + p.toString(), Toast.LENGTH_SHORT).show();

                // Xóa marker cũ nếu tồn tại
                if (currentMarker != null) {
                    mapView.getOverlays().remove(currentMarker);
                }

                // Tạo marker mới tại vị trí đã click
                Marker newMarker = createMarker(p, R.drawable.marker_blue);

                // Cập nhật marker hiện tại
                currentMarker = newMarker;

                // Thêm marker mới vào mapView
                mapView.getOverlays().add(currentMarker);
                drawRouteBetweenPoints(currentLat, currentLon, p.getLatitude(), p.getLongitude());


                // Làm mới bản đồ
                mapView.invalidate();

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(mapEventsOverlay);
    }



    private boolean isTileInBounds(int x, int y, int zoom) {
        double north = tile2lat(y, zoom);
        double south = tile2lat(y + 1, zoom);
        double west = tile2lon(x, zoom);
        double east = tile2lon(x + 1, zoom);

        return !(east < MIN_LON ||
                west > MAX_LON ||
                south > MAX_LAT ||
                north < MIN_LAT);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Bán kính trái đất (đơn vị mét)
        final double EARTH_RADIUS = 6371000;

        // Chuyển đổi độ sang radian
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Tính sự chênh lệch giữa vĩ độ và kinh độ
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Áp dụng công thức Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Tính khoảng cách (trả về đơn vị mét)
        return EARTH_RADIUS * c;
    }


    private void checkProximityAndAlert(double currentLat, double currentLon, List<Pothole> potholes) {
        for (Pothole pothole : potholes) {
            double potholeLat = pothole.getLocation().getCoordinates().getLatitude();
            double potholeLon = pothole.getLocation().getCoordinates().getLongitude();

            double distance = calculateDistance(currentLat, currentLon, potholeLat, potholeLon);

            if (distance <= 50) { // Nếu khoảng cách nhỏ hơn hoặc bằng 50m
                // Phát cảnh báo
                stopUpdatingRoute();
                Log.d("ProximityAlert", "You are near a pothole! Distance: " + distance + " meters");
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Warning: Pothole detected nearby!", Toast.LENGTH_LONG).show()
                );
                // Dừng kiểm tra nếu đã tìm thấy ổ gà gần
                break;
            }

            SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
            String latString = preferences.getString("lat", null);
            String lonString = preferences.getString("lon", null);
            double distance_location = calculateDistance(currentLat, currentLon, Double.parseDouble(latString), Double.parseDouble(lonString));
            if (distance_location <= 50) {
                stopUpdatingRoute();
                Log.d("Finished navigate", "You have arrived");

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "You have arrived", Toast.LENGTH_SHORT).show()
                );

                break;
            }
        }
    }


    private Runnable updateRouteRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRouteUpdating) {
                return;  // Nếu không cần cập nhật nữa, dừng lại
            }
            // Đọc tọa độ từ SharedPreferences
            SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
            String latString = preferences.getString("lat", null);
            String lonString = preferences.getString("lon", null);

            // Nếu tọa độ không null, thực hiện vẽ
            if (latString != null && lonString != null) {
                double savedLat = Double.parseDouble(latString);
                double savedLon = Double.parseDouble(lonString);
                drawRouteBetweenPointsAuto(currentLat, currentLon, savedLat, savedLon);
            }

            // Kiểm tra khoảng cách với các ổ gà gần
            checkProximityAndAlert(currentLat, currentLon, potholes);

            Log.d("Route", "Updating route...");
            handler.postDelayed(this, 1000);  // Lặp lại sau mỗi giây
        }
    };

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
                        potholes = gson.fromJson(json, type);
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

    private Marker createMarker(GeoPoint point, int resourceId) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setIcon(getResources().getDrawable(resourceId));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        return marker;
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

        checkAndDrawRoute();
        mapView.invalidate();
    }





    private void checkAndDrawRoute() {
        // Lấy giá trị lat và lon từ SharedPreferences
        SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        String latString = preferences.getString("lat", null);
        String lonString = preferences.getString("lon", null);

        // Thêm một flag để kiểm tra xem có nên vẽ route hay không
        boolean shouldDrawRoute = preferences.getBoolean("shouldDrawRoute", false);

        if (latString != null && lonString != null ) {
            try {
                // Chuyển đổi từ String sang double
                double savedLat = Double.parseDouble(latString);
                double savedLon = Double.parseDouble(lonString);
                GeoPoint savedGeoPoint = new GeoPoint(savedLat, savedLon);
                Marker savedLocationMarker = createMarker(savedGeoPoint, R.drawable.marker_blue);

                // Xóa marker cũ nếu tồn tại
                if (currentMarker != null) {
                    mapView.getOverlays().remove(currentMarker);
                }

                // Gán marker hiện tại
                currentMarker = savedLocationMarker;
                mapView.getOverlays().add(currentMarker);

                // Vẽ tuyến đường giữa vị trí hiện tại và vị trí đã lưu
                drawRouteBetweenPoints(currentLat, currentLon, savedLat, savedLon);

                // Cập nhật bản đồ
                mapView.invalidate();
            } catch (NumberFormatException e) {
                // Thông báo lỗi nếu dữ liệu trong SharedPreferences không hợp lệ
                Toast.makeText(getContext(), "Invalid saved location data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Thông báo nếu không có dữ liệu lat/lon trong SharedPreferences
            Toast.makeText(getContext(), "No saved location data", Toast.LENGTH_SHORT).show();
        }
    }
    private void drawRouteBetweenPoints(double startLat, double startLon, double endLat, double endLon) {
        String url = String.format(Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=polyline",
                startLon, startLat, endLon, endLat);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Xử lý lỗi nếu cần
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject shortestRoute = null;
                            double minDistance = Double.MAX_VALUE;

                            // Duyệt qua tất cả các tuyến đường và chọn tuyến ngắn nhất
                            for (int i = 0; i < routes.length(); i++) {
                                JSONObject route = routes.getJSONObject(i);
                                double distance = route.getDouble("distance");

                                // Nếu tìm thấy tuyến đường ngắn hơn, cập nhật
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    shortestRoute = route;
                                } else if (distance == minDistance) {
                                    break;
                                }
                            }

                            if (shortestRoute != null) {
                                String geometry = shortestRoute.getString("geometry");
                                double distance = shortestRoute.getDouble("distance");
                                double duration = shortestRoute.getDouble("duration");

                                List<GeoPoint> points = decodePolyline(geometry);

                                requireActivity().runOnUiThread(() -> {

                                    if (currentRouteLine != null) {
                                        mapView.getOverlays().remove(currentRouteLine);
                                    }
                                    // Vẽ tuyến đường ngắn nhất
                                    currentRouteLine = new Polyline(mapView);
                                    currentRouteLine.setColor(Color.BLUE);
                                    currentRouteLine.setWidth(10f);
                                    currentRouteLine.setPoints(points);
                                    mapView.getOverlays().add(currentRouteLine);
                                    // Tính toán bounding box (phạm vi của đoạn đường)
                                    BoundingBox boundingBox = calculateBoundingBox(points);

                                    // Điều chỉnh bản đồ để bao gồm toàn bộ đoạn đường
                                    mapView.zoomToBoundingBox(boundingBox, true);

                                    // Hiển thị thông tin về tuyến đường
                                    String routeInfo = String.format(Locale.US,
                                            "Distance: %.1f km\nEstimated time: %.0f min",
                                            distance / 1000.0, duration / 60.0);
                                    Toast.makeText(requireContext(), routeInfo, Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void stopUpdatingRoute() {
        handler.removeCallbacks(updateRouteRunnable); // Xóa callback của Runnable
        isRouteUpdating = false;
    }

    private void drawRouteBetweenPointsAuto(double startLat, double startLon, double endLat, double endLon) {
        String url = String.format(Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=polyline",
                startLon, startLat, endLon, endLat);
        Log.d("negative", "Request URL: " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("negative", "call");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("negative", "Request failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject shortestRoute = null;
                            double minDistance = Double.MAX_VALUE;

                            // Duyệt qua tất cả các tuyến đường và chọn tuyến ngắn nhất
                            for (int i = 0; i < routes.length(); i++) {
                                JSONObject route = routes.getJSONObject(i);
                                double distance = route.getDouble("distance");

                                // Nếu tìm thấy tuyến đường ngắn hơn, cập nhật
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    shortestRoute = route;
                                } else if (distance == minDistance) {
                                    break;
                                }
                            }

                            if (shortestRoute != null) {
                                String geometry = shortestRoute.getString("geometry");
                                List<GeoPoint> points = decodePolyline(geometry);

                                requireActivity().runOnUiThread(() -> {
                                    // Xóa tuyến đường cũ nếu tồn tại
                                    if (currentRouteLine != null) {
                                        mapView.getOverlays().remove(currentRouteLine);
                                    }
                                    GeoPoint myPosition = new GeoPoint(startLat, startLon);
                                    mapView.getController().animateTo(myPosition);  // Di chuyển đến vị trí người dùng
                                    mapView.getController().setZoom(18.0);  // Đặt mức zoom

                                    // Vẽ tuyến đường mới
                                    currentRouteLine = new Polyline(mapView);
                                    currentRouteLine.setColor(Color.BLUE);
                                    currentRouteLine.setWidth(10f);
                                    currentRouteLine.setPoints(points);
                                    mapView.getOverlays().add(currentRouteLine);



                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        startRealTimeLocationUpdates();

    }

    private void drawTurnByTurnRoute(double startLat, double startLon, double endLat, double endLon) {
        // Tạo URL để lấy dữ liệu chỉ dẫn từ OSRM
        String url = String.format(Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&steps=true",
                startLon, startLat, endLon, endLat);

        Log.d(TAG, "Requesting route from OSRM: " + url); // Log the URL being requested

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching route", e); // Log the error on failure
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error fetching route. Please try again.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Log.d(TAG, "Response data: " + jsonData);

                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONArray routes = json.getJSONArray("routes");

                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0); // Get the first route
                            JSONArray legs = route.getJSONArray("legs");

                            if (legs.length() > 0) {

                                startRealTimeLocationUpdates();  // Bắt đầu theo dõi vị trí người dùng
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing route data", e);
                    }
                } else {
                    Log.d(TAG, "Response was not successful or body was null.");
                }
            }
        });
    }

    private void startRealTimeLocationUpdates() {
        // Tạo LocationRequest để yêu cầu cập nhật vị trí
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(1000)  // Cập nhật mỗi giây
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, không thực hiện gì
            return;
        }

        // Bắt đầu nhận cập nhật vị trí
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();
                    Log.d(TAG, "Current location: " + currentLat + ", " + currentLon);




                }
            }
        }, Looper.getMainLooper());
    }








    private BoundingBox calculateBoundingBox(List<GeoPoint> points) {
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        // Tính toán giới hạn min/max cho vĩ độ và kinh độ
        for (GeoPoint point : points) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }


        double latDifference = maxLat - minLat;
        double lonDifference = maxLon - minLon;

        double paddingLat = latDifference * 0.1;  // 10% padding cho vĩ độ
        double paddingLon = lonDifference * 0.1;  // 10% padding cho kinh độ

        minLat -= paddingLat;
        maxLat += paddingLat;
        minLon -= paddingLon;
        maxLon += paddingLon;

        return new BoundingBox(maxLat, maxLon, minLat, minLon);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        // Xóa dữ liệu lat và lon khi Fragment bị hủy
        SharedPreferences preferences = getActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("lat"); // Xóa dữ liệu lat
        editor.remove("lon"); // Xóa dữ liệu lon
        editor.apply();
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
        // Kiểm tra quyền truy cập vị trí
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Cập nhật vị trí trên bản đồ
                                GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                                mapView.getController().animateTo(myPosition);  // Di chuyển đến vị trí người dùng
                                mapView.getController().setZoom(16.0);  // Đặt mức zoom

                                // Nếu marker cũ đã tồn tại, xóa nó
                                if (myLocationMarker != null) {
                                    mapView.getOverlays().remove(myLocationMarker);
                                }

                                // Tạo marker mới tại vị trí của người dùng
                                myLocationMarker = new Marker(mapView);
                                myLocationMarker.setPosition(myPosition);
                                myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                myLocationMarker.setTitle("You're here");

                                // Thêm marker vào overlays của mapView
                                mapView.getOverlays().add(myLocationMarker);
                            } else {
                                Toast.makeText(requireContext(), "Finding your location...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            requestPermissions();  // Yêu cầu quyền truy cập vị trí nếu chưa cấp
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Cập nhật vị trí trên bản đồ khi vị trí thay đổi
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