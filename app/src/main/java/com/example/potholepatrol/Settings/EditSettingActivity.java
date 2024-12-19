package com.example.potholepatrol.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.UserProfileResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditSettingActivity extends AppCompatActivity {

    private EditText nameEdit, emailEdit, passwordEdit, newpasswordEdit, dobEdit, countryEdit;
    private LinearLayout confirmButton;
    private ImageView backButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_edit_profile);
        setupStatusBar();
        initializeViews();
        setupListeners();

        // Tải thông tin người dùng từ server
        loadUserProfile();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", ""); // Trả về token hoặc chuỗi rỗng nếu không có
    }

    private void loadUserProfile() {
        String token = "Bearer " + getAccessToken();

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.getUserProfile(token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    // Kiểm tra null và gán hint cho các trường
                    nameEdit.setText(profile.getUsername() != null ? profile.getUsername() : "");
                    nameEdit.setHint(profile.getUsername() == null ? "Please update your name" : "");

                    emailEdit.setText(profile.getEmail() != null ? profile.getEmail() : "");
                    emailEdit.setHint(profile.getEmail() == null ? "Please update your email" : "");

                    dobEdit.setText(profile.getBirthday() != null ? profile.getBirthday().split("T")[0] : "");
                    dobEdit.setHint(profile.getBirthday() == null ? "Please update your date of birth" : "");

                    countryEdit.setText(profile.getCountry() != null ? profile.getCountry() : "");
                    countryEdit.setHint(profile.getCountry() == null ? "Please update your country" : "");

                    // Email không được chỉnh sửa
                    emailEdit.setEnabled(false);
                    emailEdit.setAlpha(0.5f);
                } else {
                    showStatusDialog(false, "Failed to load user data. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                showStatusDialog(false, "Network error: " + t.getMessage());
            }
        });
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
        newpasswordEdit = findViewById(R.id.edit_text_new_password);
        dobEdit = findViewById(R.id.edit_text_date_of_birth);
        countryEdit = findViewById(R.id.edit_text_country);
        backButton = findViewById(R.id.ic_back);
        // Initialize buttons
        confirmButton = findViewById(R.id.btn_confirm);

        // Mặc định tắt ô nhập mật khẩu mới
        newpasswordEdit.setEnabled(false);
    }

    private void setupListeners() {
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kích hoạt ô nhập mật khẩu mới nếu currentPassword không rỗng
                newpasswordEdit.setEnabled(s.length() > 0);
                newpasswordEdit.setAlpha(s.length() > 0 ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        backButton.setOnClickListener(v -> finish());

        confirmButton.setOnClickListener(v -> validateAndSaveProfile());
    }

    private void validateAndSaveProfile() {
        String name = nameEdit.getText().toString().trim();
        String dob = dobEdit.getText().toString().trim();
        String country = countryEdit.getText().toString().trim();
        String currentPassword = passwordEdit.getText().toString().trim();
        String newPassword = newpasswordEdit.getText().toString().trim();

        // Chuẩn bị Map chứa các trường cần gửi
        Map<String, Object> fields = new HashMap<>();

        // Thêm thông tin cá nhân nếu có
        if (!name.isEmpty() && !name.equals("Please update your name")) {
            fields.put("username", name);
        }

        if (!dob.isEmpty() && !dob.equals("Please update your date of birth") && isValidDate(dob)) {
            fields.put("birthday", formatDateForApi(dob)); // Định dạng ngày
        } else if (!isValidDate(dob)) {
            showStatusDialog(false, "Invalid date format. Please use DD/MM/YYYY.");
            return;
        }

        if (!country.isEmpty() && !country.equals("Please update your country")) {
            fields.put("country", country);
        }

        // Thêm đổi mật khẩu nếu có
        if (!currentPassword.isEmpty()) {
            if (newPassword.isEmpty() || newPassword.length() < 6) {
                showStatusDialog(false, "New password must be at least 6 characters.");
                return;
            }
            fields.put("currentPassword", currentPassword);
            fields.put("newPassword", newPassword);
        }

        // Kiểm tra nếu không có trường nào được gửi
        if (fields.isEmpty()) {
            showStatusDialog(false, "No changes to save.");
            return;
        }

        // Gửi API
        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.updateUserProfile("Bearer " + getAccessToken(), fields).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    showStatusDialog(true, "Profile updated successfully.");
                } else {
                    showStatusDialog(false, "Failed to update profile. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showStatusDialog(false, "Network error: " + t.getMessage());
            }
        });
    }

    private String formatDateForApi(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return apiFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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

            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;

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
