package com.example.potholepatrol.Settings;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditSettingActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, passwordEdit, dobEdit, countryEdit;
    private ImageView profileImage, backButton, cameraButton;
    private LinearLayout confirmButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_edit_profile);
        setupStatusBar();
        initializeViews();
        loadUserData();
        setupListeners();
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

    private void initializeViews() {
        // Initialize EditTexts
        nameEdit = findViewById(R.id.edit_text_name);
        emailEdit = findViewById(R.id.edit_text_email);
        passwordEdit = findViewById(R.id.edit_text_password);
        dobEdit = findViewById(R.id.edit_text_date_of_birth);
        countryEdit = findViewById(R.id.edit_text_country);

        // Initialize buttons and images
        profileImage = findViewById(R.id.image_edit_profile);
        backButton = findViewById(R.id.ic_back);
        cameraButton = findViewById(R.id.ic_camera);
        confirmButton = findViewById(R.id.btn_confirm);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        cameraButton.setOnClickListener(v -> {
            // TODO: Implement image picker functionality
            showStatusDialog(false, "Image upload feature coming soon");
        });

        confirmButton.setOnClickListener(v -> validateAndSaveProfile());
    }

    private void loadUserData() {
        nameEdit.setText(sharedPreferences.getString("name", ""));
        emailEdit.setText(sharedPreferences.getString("email", ""));
        dobEdit.setText(sharedPreferences.getString("dob", ""));
        countryEdit.setText(sharedPreferences.getString("country", ""));
        // Don't load password for security reasons
    }

    private void validateAndSaveProfile() {
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String dob = dobEdit.getText().toString().trim();
        String country = countryEdit.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            showStatusDialog(false, "Please enter your name");
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showStatusDialog(false, "Please enter a valid email address");
            return;
        }

        if (password.length() < 6 && !password.isEmpty()) {
            showStatusDialog(false, "Password must be at least 6 characters");
            return;
        }

        if (!isValidDate(dob)) {
            showStatusDialog(false, "Please enter a valid date (DD/MM/YYYY)");
            return;
        }

        if (country.isEmpty()) {
            showStatusDialog(false, "Please enter your country");
            return;
        }

        // Save data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        if (!password.isEmpty()) {
            editor.putString("password", password); // In production, use proper encryption
        }
        editor.putString("dob", dob);
        editor.putString("country", country);
        editor.apply();

        showStatusDialog(true, "Profile updated successfully");
    }

    private boolean isValidDate(String date) {
        if (date.isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
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
        tvStatus.setText("Profile Update");
        tvStatusMessage.setText(message);

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