package com.example.potholepatrol.Settings;

import android.app.Dialog;
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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        // Initialize views
        ImageView btnBack = findViewById(R.id.btn_back);
        soundOption = findViewById(R.id.btn_sound);
        vibrateOption = findViewById(R.id.btn_vibrate);
        btnSave = findViewById(R.id.btn_save);

        // Load saved preference
        loadSavedPreference();

        // Back button handler
        btnBack.setOnClickListener(v -> finish());

        // Sound option handler
        soundOption.setOnClickListener(v -> {
            selectPreference("Sound");
        });

        // Vibrate option handler
        vibrateOption.setOnClickListener(v -> {
            selectPreference("Vibrate");
        });

        // Save button handler
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

        showStatusDialog(true, "Notification preference saved: " + selectedPreference);
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

            // Add extra padding (e.g., 8dp converted to pixels)
            int extraPadding = (int) (8 * getResources().getDisplayMetrics().density);

            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = statusBarHeight + extraPadding; // Position below status bar with padding

            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvStatus.setText("Notification Settings");
        tvStatusMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (message.startsWith("Notification preference saved")) {
                    finish();
                }
            }
        }, 2000);
    }

    private void loadSavedPreference() {
        selectedPreference = sharedPreferences.getString("alert_preference", "Sound");
        updateButtonState();
    }

    private void updateButtonState() {
        // Reset styles
        soundOption.setBackgroundResource(R.drawable.setting_frame);
        vibrateOption.setBackgroundResource(R.drawable.setting_frame);
        soundOption.setTextColor(getResources().getColor(R.color.black));
        vibrateOption.setTextColor(getResources().getColor(R.color.black));

        // Highlight selected option
        if ("Sound".equals(selectedPreference)) {
            soundOption.setBackgroundResource(R.drawable.selected_button_background);
            soundOption.setTextColor(getResources().getColor(R.color.white));
        } else {
            vibrateOption.setBackgroundResource(R.drawable.selected_button_background);
            vibrateOption.setTextColor(getResources().getColor(R.color.white));
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