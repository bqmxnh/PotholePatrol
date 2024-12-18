package com.example.potholepatrol.Settings;

import android.content.SharedPreferences;
import android.graphics.Color;
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

public class NotificationSettingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView soundOption, vibrateOption;
    private Button btnSave;
    private String selectedPreference = "Sound"; // Default preference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notifications);

        setupStatusBar();

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        // Ánh xạ view
        ImageView btnBack = findViewById(R.id.btn_back);
        soundOption = findViewById(R.id.btn_sound);
        vibrateOption = findViewById(R.id.btn_vibrate);
        btnSave = findViewById(R.id.btn_save);

        // Load preference đã lưu
        loadSavedPreference();

        // Xử lý sự kiện Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý chọn Sound
        soundOption.setOnClickListener(v -> selectPreference("Sound"));

        // Xử lý chọn Vibrate
        vibrateOption.setOnClickListener(v -> selectPreference("Vibrate"));

        // Xử lý Save
        btnSave.setOnClickListener(v -> savePreference());
    }

    private void selectPreference(String preference) {
        selectedPreference = preference;
        updateButtonState();
    }

    private void savePreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alert_preference", selectedPreference);
        editor.apply();

        // Hiển thị thông báo
        Toast.makeText(this, "Preference saved: " + selectedPreference, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadSavedPreference() {
        selectedPreference = sharedPreferences.getString("alert_preference", "Sound");
        updateButtonState();
    }

    private void updateButtonState() {
        // Reset styles
        soundOption.setBackgroundResource(R.drawable.setting_frame);
        vibrateOption.setBackgroundResource(R.drawable.setting_frame);

        // Highlight selected option
        if ("Sound".equals(selectedPreference)) {
            soundOption.setBackgroundResource(R.drawable.selected_button_background);
        } else {
            vibrateOption.setBackgroundResource(R.drawable.selected_button_background);
        }
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
