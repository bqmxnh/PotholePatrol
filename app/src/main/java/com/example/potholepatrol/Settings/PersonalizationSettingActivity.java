package com.example.potholepatrol.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;

public class PersonalizationSettingActivity extends AppCompatActivity {
    private TextView btnLow, btnMedium, btnHigh;
    private Button btnSave;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;
    private String selectedSensitivity = "medium"; // Giá trị mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_personalization);
        // Xử lý Status Bar
        setupStatusBar();
        // Ánh xạ các nút
        btnLow = findViewById(R.id.btn_low);
        btnMedium = findViewById(R.id.btn_medium);
        btnHigh = findViewById(R.id.btn_high);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.ic_back);

        // SharedPreferences lưu trữ sensitivity
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        // Load sensitivity đã lưu
        loadSavedSensitivity();

        // Xử lý click cho từng nút
        btnLow.setOnClickListener(v -> selectSensitivity("low"));
        btnMedium.setOnClickListener(v -> selectSensitivity("medium"));
        btnHigh.setOnClickListener(v -> selectSensitivity("high"));

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> saveSensitivity());

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupStatusBar() {
        Window window = getWindow();

        // Làm trong suốt status bar và kích hoạt full-screen mode
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void selectSensitivity(String sensitivity) {
        selectedSensitivity = sensitivity;
        updateButtonState(sensitivity);
    }

    private void saveSensitivity() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("detection_sensitivity", selectedSensitivity);
        editor.apply();

        // Hiển thị thông báo
        Toast.makeText(this, "Sensitivity saved: " + selectedSensitivity, Toast.LENGTH_SHORT).show();

        // Thoát màn hình cài đặt sau khi lưu
        finish();
    }

    private void loadSavedSensitivity() {
        selectedSensitivity = sharedPreferences.getString("detection_sensitivity", "medium");
        updateButtonState(selectedSensitivity);
    }

    private void updateButtonState(String sensitivity) {
        // Reset màu
        resetButtonStyles();
        switch (sensitivity) {
            case "low":
                btnLow.setBackgroundResource(R.drawable.selected_button_background);
                break;
            case "medium":
                btnMedium.setBackgroundResource(R.drawable.selected_button_background);
                break;
            case "high":
                btnHigh.setBackgroundResource(R.drawable.selected_button_background);
                break;
        }
    }

    private void resetButtonStyles() {
        btnLow.setBackgroundResource(R.drawable.setting_frame);
        btnMedium.setBackgroundResource(R.drawable.setting_frame);
        btnHigh.setBackgroundResource(R.drawable.setting_frame);
    }
}
