package com.example.potholepatrol.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        confirmButton.setAlpha(isDataSharingEnabled ? 1.0f : 0.5f);

        if (!isDataSharingEnabled) {
            showStatusDialog(false, "Please enable data sharing in Privacy Settings to report potholes");
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
        if (!sharedPreferences.getBoolean(KEY_DATA_SHARING, false)) {
            showStatusDialog(false, "Data sharing must be enabled to report potholes");
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
                            showStatusDialog(true, "Pothole reported successfully");
                        } else {
                            showStatusDialog(false, "Failed to report pothole");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showStatusDialog(false, "Error: " + t.getMessage());
                    }
                });
    }

    private boolean validateInput() {
        String message = null;
        if (locationEdit.getText().toString().trim().isEmpty()) {
            message = "Please enter location";
        } else if (selectedDimension.isEmpty()) {
            message = "Please select dimension";
        } else if (selectedDepth.isEmpty()) {
            message = "Please select depth";
        } else if (selectedShape.isEmpty()) {
            message = "Please select shape";
        } else if (selectedSeverity.isEmpty()) {
            message = "Please select severity level";
        }

        if (message != null) {
            showStatusDialog(false, message);
            return false;
        }
        return true;
    }
    private void showStatusDialog(boolean isSuccess, String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
// Get status bar height
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // Add extra padding (8dp)
            int extraPadding = (int) (8 * getResources().getDisplayMetrics().density);
            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = statusBarHeight + extraPadding;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvStatus.setText("Pothole Report");
        tvStatusMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (isSuccess && message.equals("Pothole reported successfully")) {
                    finish();
                }
            }
        }, 2000);
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
