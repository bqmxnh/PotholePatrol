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

public class PrivacySettingActivity extends AppCompatActivity {

    private TextView btnOn, btnOff;
    private Button btnSave;
    private ImageView icBack;
    private boolean isSharingOn = false; // Trạng thái mặc định của chia sẻ dữ liệu

    private static final String PRIVACY_PREFS = "PrivacySettings";
    private static final String KEY_DATA_SHARING = "isSharingOn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_privacy);
        setupStatusBar();

        // Load current setting
        SharedPreferences prefs = getSharedPreferences(PRIVACY_PREFS, MODE_PRIVATE);
        isSharingOn = prefs.getBoolean(KEY_DATA_SHARING, false);

        initializeViews();
        updateSharingButtons();
    }

    private void initializeViews() {
        btnOn = findViewById(R.id.btn_on);
        btnOff = findViewById(R.id.btn_off);
        btnSave = findViewById(R.id.btn_save);
        icBack = findViewById(R.id.ic_back);

        icBack.setOnClickListener(v -> onBackPressed());

        btnOn.setOnClickListener(v -> {
            isSharingOn = true;
            updateSharingButtons();
        });

        btnOff.setOnClickListener(v -> {
            isSharingOn = false;
            updateSharingButtons();
        });

        btnSave.setOnClickListener(v -> savePrivacySettings());
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

    private void updateSharingButtons() {
        if (isSharingOn) {
            btnOn.setBackgroundResource(R.drawable.selected_button_background);
            btnOff.setBackgroundResource(R.drawable.setting_frame);
        } else {
            btnOff.setBackgroundResource(R.drawable.selected_button_background);
            btnOn.setBackgroundResource(R.drawable.setting_frame);
        }
    }

    private void savePrivacySettings() {
        SharedPreferences prefs = getSharedPreferences(PRIVACY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DATA_SHARING, isSharingOn);
        editor.apply();

        String message = isSharingOn ?
                "Data sharing is turned ON" :
                "Data sharing is turned OFF";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        finish();
    }
}
