package com.example.potholepatrol.Settings;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;

public class NotificationSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notifications);

        // Set up back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Set up sound option
        TextView soundOption = findViewById(R.id.button_sound);
        soundOption.setOnClickListener(v -> {
            // Handle sound option click
        });

        // Set up vibrate option
        TextView vibrateOption = findViewById(R.id.button_vibrate);
        vibrateOption.setOnClickListener(v -> {
            // Handle vibrate option click
        });

        // Set up save button
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            showStatusDialog(true, "Save Successful");
        });
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

            // Set position and background
            layoutParams.gravity = Gravity.TOP;
            layoutParams.y = 0;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Add dim amount
            layoutParams.dimAmount = 0.5f;
            window.setAttributes(layoutParams);
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvLoginStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvLoginStatus.setText("Save");
        tvStatusMessage.setText(isSuccess ? "Successful" : message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (isSuccess) {
                    finish();
                }
            }
        }, 2000);
    }
}