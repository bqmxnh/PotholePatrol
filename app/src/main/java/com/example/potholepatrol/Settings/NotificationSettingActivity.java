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

public class NotificationSettingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView soundOption, vibrateOption;
    private Button btnSave;
    private String selectedPreference = "Sound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notifications);

        setupStatusBar();

        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        ImageView btnBack = findViewById(R.id.btn_back);
        soundOption = findViewById(R.id.btn_sound);
        vibrateOption = findViewById(R.id.btn_vibrate);
        btnSave = findViewById(R.id.btn_save);

        loadSavedPreference();

        btnBack.setOnClickListener(v -> finish());

        soundOption.setOnClickListener(v -> {
            selectPreference("Sound");
        });

        vibrateOption.setOnClickListener(v -> {
            selectPreference("Vibrate");
        });

        btnSave.setOnClickListener(v -> savePreference());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
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

        // Cho phép dialog đóng khi nhấn ra ngoài
        dialog.setCancelable(true);

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
        tvStatus.setText("Notification Settings");
        tvStatusMessage.setText(message);

        // Lắng nghe sự kiện khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> {
            if (message.startsWith("Notification preference saved")) {
                finish();
            }
        });

        dialog.show();

        // Tự động đóng sau 2 giây
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 2000);
    }


    private void loadSavedPreference() {
        selectedPreference = sharedPreferences.getString("alert_preference", "Sound");
        updateButtonState();
    }

    private void updateButtonState() {
        soundOption.setBackgroundResource(R.drawable.setting_frame);
        vibrateOption.setBackgroundResource(R.drawable.setting_frame);
        soundOption.setTextColor(getResources().getColor(R.color.black));
        vibrateOption.setTextColor(getResources().getColor(R.color.black));

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