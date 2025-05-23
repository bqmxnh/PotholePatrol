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

public class PrivacySettingActivity extends AppCompatActivity {

    private TextView btnOn, btnOff;
    private Button btnSave;
    private ImageView icBack;
    private boolean isSharingOn = false;

    private static final String PRIVACY_PREFS = "PrivacySettings";
    private static final String KEY_DATA_SHARING = "isSharingOn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_privacy);
        setupStatusBar();

        SharedPreferences prefs = getSharedPreferences(PRIVACY_PREFS, MODE_PRIVATE);
        isSharingOn = prefs.getBoolean(KEY_DATA_SHARING, false);

        initializeViews();
        updateSharingButtons();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
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

        showStatusDialog(true, isSharingOn ? "Data sharing is turned ON" : "Data sharing is turned OFF");
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
        tvStatus.setText("Privacy Settings");
        tvStatusMessage.setText(message);

        // Lắng nghe sự kiện khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> {
            finish(); // Đảm bảo kết thúc hoạt động khi dialog bị đóng
        });

        dialog.show();

        // Tự động đóng sau 2 giây
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 2000);
    }

}