package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentMap extends Fragment {
    private MapView mapView;
    private static final String MBTILES_URL = "http://192.168.254.134:8000/";
    private static final String MBTILES_FILENAME = "dhqg-tphcm.mbtiles";
    private static final String JSON_FILENAME = "dhqg-tphcm.json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().load(requireContext(),
                requireContext().getSharedPreferences("osmdroid", 0));

        View view = inflater.inflate(R.layout.activity_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        // Tải file MBTiles
        String mbtilesPath = requireContext().getExternalFilesDir(null) + "/" + MBTILES_FILENAME;
        downloadMbtiles(MBTILES_URL + MBTILES_FILENAME, mbtilesPath);

        // Tải file JSON
        String jsonPath = requireContext().getExternalFilesDir(null) + "/" + JSON_FILENAME;
        downloadJson(MBTILES_URL + JSON_FILENAME, jsonPath);

        // Xử lý sự kiện Zoom
        ImageView btnZoomIn = view.findViewById(R.id.button_zoom_in);
        ImageView btnZoomOut = view.findViewById(R.id.button_zoom_out);

        btnZoomIn.setOnClickListener(v -> mapView.getController().zoomIn());
        btnZoomOut.setOnClickListener(v -> mapView.getController().zoomOut());

        return view;
    }

    private void downloadMbtiles(String url, String outputPath) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();


        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MBTiles", "Download failed: " + e.getMessage());
                // Retry the download with exponential backoff
                retryDownloadWithBackoff(url, outputPath, 1, 1000);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("MBTiles", "Server returned error: " + response.code());
                    // Retry the download with exponential backoff
                    retryDownloadWithBackoff(url, outputPath, 1, 1000);
                    return;
                }

                if (response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }



                    File downloadedFile = new File(outputPath);
                        if (downloadedFile.length() > 0) {
                            Log.i("MBTiles", "File downloaded successfully: " + outputPath);
                            initializeMap(outputPath);
                        } else {
                            Log.e("MBTiles", "Downloaded file is empty.");
                            // Retry the download with exponential backoff
                            retryDownloadWithBackoff(url, outputPath, 1, 1000);
                        }
                    } catch (Exception e) {
                        Log.e("MBTiles", "Error saving MBTiles file: " + e.getMessage());
                        // Retry the download with exponential backoff
                        retryDownloadWithBackoff(url, outputPath, 1, 1000);
                    }
                } else {
                    Log.e("MBTiles", "Response body is null.");
                    // Retry the download with exponential backoff
                    retryDownloadWithBackoff(url, outputPath, 1, 1000);
                }
            }
        });
    }

    private void retryDownloadWithBackoff(String url, String outputPath, int retryCount, int delayMs) {
        if (retryCount > 5) {
            Log.e("MBTiles", "Maximum number of retries reached. Unable to download MBTiles file.");
            return;
        }

        Log.d("MBTiles", "Retrying download, attempt #" + retryCount);

        new Handler(Looper.getMainLooper()).postDelayed(() -> downloadMbtiles(url, outputPath), delayMs);
    }



    private void initializeMap(String mbtilesPath) {
        File file = new File(mbtilesPath);
        if (file.exists()) {
            requireActivity().runOnUiThread(() -> {
                XYTileSource tileSource = new XYTileSource(
                        "MBTiles",
                        0, 20, 256,
                        ".png",
                        new String[]{"http://192.168.254.134:8000"}
                );

                mapView.setTileSource(tileSource);
                mapView.getTileProvider().setTileSource(tileSource);
                mapView.getController().setZoom(15.0);
                mapView.getController().setCenter(new GeoPoint(10.8731, 106.8044));

                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(10.8731, 106.8044));
                marker.setTitle("ĐHQG-HCM");
                mapView.getOverlays().add(marker);
            });
        } else {
            Log.e("MBTiles", "File not found: " + mbtilesPath);
        }
    }

    private void downloadJson(String url, String outputPath) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("JSON", "Download failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        loadJsonFile(outputPath); // Load JSON after download
                    }
                }
            }
        });
    }

    private void loadJsonFile(String jsonFilePath) {
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            Log.e("JSON", "JSON file not found: " + jsonFilePath);
            return;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            String jsonContent = new String(buffer, "UTF-8");

            // Kiểm tra xem JSON là object hay array
            if (jsonContent.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonContent);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    double latitude = jsonObject.getDouble("latitude");
                    double longitude = jsonObject.getDouble("longitude");
                    String title = jsonObject.getString("title");

                    requireActivity().runOnUiThread(() -> {
                        Marker marker = new Marker(mapView);
                        marker.setPosition(new GeoPoint(latitude, longitude));
                        marker.setTitle(title);
                        mapView.getOverlays().add(marker);
                    });
                }
            } else {
                JSONObject jsonObject = new JSONObject(jsonContent);

                // Nếu JSON không chứa dữ liệu cần thiết, bạn có thể ghi log hoặc xử lý tại đây
                Log.i("JSON", "Received JSON Object instead of Array: " + jsonObject.toString());
            }
        } catch (Exception e) {
            Log.e("JSON", "Error reading JSON file: " + e.getMessage());
        }
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
