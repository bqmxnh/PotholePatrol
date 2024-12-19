package com.example.potholepatrol.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.potholepatrol.R;
import com.example.potholepatrol.model.PotholeRequest;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPotholeActivity extends AppCompatActivity {
    private EditText locationEdit;
    private TextView gpsText;
    private Switch damageSwitch;
    private AppCompatButton confirmButton;
    private ImageButton backButton;
    private SharedPreferences sharedPreferences;
    private static final String PRIVACY_PREFS = "PrivacySettings";
    private static final String KEY_DATA_SHARING = "isSharingOn";


    // Dimension buttons
    private TextView compactBtn, averageBtn, largeBtn;
    private String selectedDimension = "";

    // Depth buttons
    private TextView shallowBtn, noticeableBtn, deepBtn;
    private String selectedDepth = "";

    // Shape buttons
    private TextView uniformBtn, unevenBtn, jaggedBtn;
    private String selectedShape = "";

    // Severity buttons
    private TextView lowBtn, mediumBtn, highBtn;
    private String selectedSeverity = "";

    // Location coordinates
    private double latitude = 0.0;
    private double longitude = 0.0;

    // LocationManager and LocationListener
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole_report);

        sharedPreferences = getSharedPreferences(PRIVACY_PREFS, MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        setupLocationServices();
        updateConfirmButtonState();
    }



    private void initializeViews() {
        locationEdit = findViewById(R.id.et_location);
        gpsText = findViewById(R.id.tv_gps);
        damageSwitch = findViewById(R.id.switch_damage);
        confirmButton = findViewById(R.id.btn_confirm);
        backButton = findViewById(R.id.btn_back);

        // Initialize dimension buttons
        compactBtn = findViewById(R.id.btn_compact);
        averageBtn = findViewById(R.id.btn_average);
        largeBtn = findViewById(R.id.btn_large);

        // Initialize depth buttons
        shallowBtn = findViewById(R.id.btn_shallow);
        noticeableBtn = findViewById(R.id.btn_noticeable);
        deepBtn = findViewById(R.id.btn_deep);

        // Initialize shape buttons
        uniformBtn = findViewById(R.id.btn_uniform);
        unevenBtn = findViewById(R.id.btn_uneven);
        jaggedBtn = findViewById(R.id.btn_jagged);

        // Initialize severity buttons
        lowBtn = findViewById(R.id.btn_low);
        mediumBtn = findViewById(R.id.btn_medium);
        highBtn = findViewById(R.id.btn_high);

        confirmButton.setEnabled(false);

    }

    private void updateConfirmButtonState() {
        boolean isDataSharingEnabled = sharedPreferences.getBoolean(KEY_DATA_SHARING, false);
        confirmButton.setEnabled(isDataSharingEnabled);

        // Optionally update the button's appearance
        confirmButton.setAlpha(isDataSharingEnabled ? 1.0f : 0.5f);

        // Show a message if data sharing is disabled
        if (!isDataSharingEnabled) {
            Toast.makeText(this,
                    "Please enable data sharing in Privacy Settings to report potholes",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        // Set up dimension button listeners
        setupDimensionButtons();

        // Set up depth button listeners
        setupDepthButtons();

        // Set up shape button listeners
        setupShapeButtons();

        // Set up severity button listeners
        setupSeverityButtons();

        // Confirm button listener
        confirmButton.setOnClickListener(v -> submitPotholeReport());
    }

    private void setupDimensionButtons() {
        compactBtn.setOnClickListener(v -> {
            selectedDimension = "Compact";
            updateButtonSelection(new TextView[]{compactBtn, averageBtn, largeBtn}, compactBtn);
        });

        averageBtn.setOnClickListener(v -> {
            selectedDimension = "Average";
            updateButtonSelection(new TextView[]{compactBtn, averageBtn, largeBtn}, averageBtn);
        });

        largeBtn.setOnClickListener(v -> {
            selectedDimension = "Large";
            updateButtonSelection(new TextView[]{compactBtn, averageBtn, largeBtn}, largeBtn);
        });
    }

    private void setupDepthButtons() {
        shallowBtn.setOnClickListener(v -> {
            selectedDepth = "Shallow";
            updateButtonSelection(new TextView[]{shallowBtn, noticeableBtn, deepBtn}, shallowBtn);
        });

        noticeableBtn.setOnClickListener(v -> {
            selectedDepth = "Noticeable";
            updateButtonSelection(new TextView[]{shallowBtn, noticeableBtn, deepBtn}, noticeableBtn);
        });

        deepBtn.setOnClickListener(v -> {
            selectedDepth = "Deep";
            updateButtonSelection(new TextView[]{shallowBtn, noticeableBtn, deepBtn}, deepBtn);
        });
    }

    private void setupShapeButtons() {
        uniformBtn.setOnClickListener(v -> {
            selectedShape = "Uniform";
            updateButtonSelection(new TextView[]{uniformBtn, unevenBtn, jaggedBtn}, uniformBtn);
        });

        unevenBtn.setOnClickListener(v -> {
            selectedShape = "Uneven";
            updateButtonSelection(new TextView[]{uniformBtn, unevenBtn, jaggedBtn}, unevenBtn);
        });

        jaggedBtn.setOnClickListener(v -> {
            selectedShape = "Jagged";
            updateButtonSelection(new TextView[]{uniformBtn, unevenBtn, jaggedBtn}, jaggedBtn);
        });
    }

    private void setupSeverityButtons() {
        lowBtn.setOnClickListener(v -> {
            selectedSeverity = "Low";
            updateButtonSelection(new TextView[]{lowBtn, mediumBtn, highBtn}, lowBtn);
        });

        mediumBtn.setOnClickListener(v -> {
            selectedSeverity = "Medium";
            updateButtonSelection(new TextView[]{lowBtn, mediumBtn, highBtn}, mediumBtn);
        });

        highBtn.setOnClickListener(v -> {
            selectedSeverity = "High";
            updateButtonSelection(new TextView[]{lowBtn, mediumBtn, highBtn}, highBtn);
        });
    }

    private void updateButtonSelection(TextView[] buttons, TextView selectedButton) {
        for (TextView button : buttons) {
            button.setBackgroundResource(R.drawable.setting_frame);
            button.setTextColor(getResources().getColor(R.color.black));
        }
        selectedButton.setBackgroundResource(R.drawable.selected_button_background);
        selectedButton.setTextColor(getResources().getColor(R.color.white));
    }

    private void setupLocationServices() {
        // Initialize LocationManager and check if GPS is enabled
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Permission for location is not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Update the coordinates
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // Display the coordinates in the TextView
            gpsText.setText(String.format("GPS: %.6f, %.6f", latitude, longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void submitPotholeReport() {
        // First check if data sharing is enabled
        if (!sharedPreferences.getBoolean(KEY_DATA_SHARING, false)) {
            Toast.makeText(this,
                    "Data sharing must be enabled to report potholes",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInput()) {
            return;
        }

        // Create the request object
        PotholeRequest.Location location = new PotholeRequest.Location(
                locationEdit.getText().toString(),
                latitude,
                longitude
        );

        PotholeRequest.Description description = new PotholeRequest.Description(
                selectedDimension,
                selectedDepth,
                selectedShape
        );

        PotholeRequest.Severity severity = new PotholeRequest.Severity(
                selectedSeverity,
                damageSwitch.isChecked()
        );

        PotholeRequest potholeRequest = new PotholeRequest(location, description, severity);

        // Make the API call
        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.addPothole("Bearer " + getStoredToken(), potholeRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddPotholeActivity.this,
                                    "Pothole reported successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddPotholeActivity.this,
                                    "Failed to report pothole", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddPotholeActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput() {
        if (locationEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDimension.isEmpty()) {
            Toast.makeText(this, "Please select dimension", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDepth.isEmpty()) {
            Toast.makeText(this, "Please select depth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedShape.isEmpty()) {
            Toast.makeText(this, "Please select shape", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedSeverity.isEmpty()) {
            Toast.makeText(this, "Please select severity level", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getStoredToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", ""); // Retrieve the stored access token
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateConfirmButtonState();
    }
}
