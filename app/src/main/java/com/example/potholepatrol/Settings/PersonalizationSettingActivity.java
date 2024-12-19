package com.example.potholepatrol.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;

public class PersonalizationSettingActivity extends AppCompatActivity {
    private TextView btnLow, btnMedium, btnHigh;
    private Button btnSave;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;
    private String selectedSensitivity = "medium"; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personalization);

        setupStatusBar();
        initializeViews();
        setupListeners();
        loadSavedSensitivity();
    }

    private void initializeViews() {
        btnLow = findViewById(R.id.btn_low);
        btnMedium = findViewById(R.id.btn_medium);
        btnHigh = findViewById(R.id.btn_high);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.ic_back);
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
    }

    private void setupListeners() {
        btnLow.setOnClickListener(v -> {
            selectSensitivity("low");
            showStatusDialog(true, "Low sensitivity selected");
        });

        btnMedium.setOnClickListener(v -> {
            selectSensitivity("medium");
            showStatusDialog(true, "Medium sensitivity selected");
        });

        btnHigh.setOnClickListener(v -> {
            selectSensitivity("high");
            showStatusDialog(true, "High sensitivity selected");
        });

        btnSave.setOnClickListener(v -> saveSensitivity());
        btnBack.setOnClickListener(v -> onBackPressed());
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

    private void selectSensitivity(String sensitivity) {
        selectedSensitivity = sensitivity;
        updateButtonState(sensitivity);
    }

    private void saveSensitivity() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("detection_sensitivity", selectedSensitivity);
        editor.apply();

        showStatusDialog(true, "Detection sensitivity saved: " +
                selectedSensitivity.substring(0, 1).toUpperCase() + selectedSensitivity.substring(1));
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

            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = 0;

            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvStatus.setText("Detection Settings");
        tvStatusMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (message.startsWith("Detection sensitivity saved")) {
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }

    private void loadSavedSensitivity() {
        selectedSensitivity = sharedPreferences.getString("detection_sensitivity", "medium");
        updateButtonState(selectedSensitivity);
    }

    private void updateButtonState(String sensitivity) {
        resetButtonStyles();
        TextView selectedButton = btnMedium; // default

        switch (sensitivity) {
            case "low":
                selectedButton = btnLow;
                break;
            case "medium":
                selectedButton = btnMedium;
                break;
            case "high":
                selectedButton = btnHigh;
                break;
        }

        selectedButton.setBackgroundResource(R.drawable.selected_button_background);
        selectedButton.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetButtonStyles() {
        btnLow.setBackgroundResource(R.drawable.setting_frame);
        btnMedium.setBackgroundResource(R.drawable.setting_frame);
        btnHigh.setBackgroundResource(R.drawable.setting_frame);

        btnLow.setTextColor(getResources().getColor(R.color.black));
        btnMedium.setTextColor(getResources().getColor(R.color.black));
        btnHigh.setTextColor(getResources().getColor(R.color.black));
    }
}