package com.example.potholepatrol.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;


import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;


import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.LoginRequest;

import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private TextView tvForgot, tvCreate;
    private View viButton, enButton;
    private TextView viText, enText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Setup language selector
        setupLanguageButtons();

        // Setup other click listeners
        setupClickListeners();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }



    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgot = findViewById(R.id.tvForgot);
        tvCreate = findViewById(R.id.tvCreate);

        // Language selector views
        viButton = findViewById(R.id.vi_button);
        enButton = findViewById(R.id.en_button);
        viText = findViewById(R.id.text_vi);
        enText = findViewById(R.id.text_en);
    }

    private void setupLanguageButtons() {
        // Set click listeners for language buttons
        viButton.setOnClickListener(v -> {
            showLanguageDialog("vi");
        });

        enButton.setOnClickListener(v -> {
            showLanguageDialog("en");
        });

        // Set initial state
        String currentLang = getCurrentLanguage();
        updateLanguageUI(currentLang.equals("vi"));
    }

    private void showLanguageDialog(String languageCode) {
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_language, null);
            TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
            TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
            Button btnYes = dialogView.findViewById(R.id.btnYes);
            Button btnNo = dialogView.findViewById(R.id.btnNo);

            dialogTitle.setText(getString(R.string.language_change_title));
            dialogMessage.setText(getString(R.string.language_change_message));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            btnYes.setOnClickListener(v -> {
                dialog.dismiss();
                setNewLocale(languageCode);
                updateLanguageUI(languageCode.equals("vi"));
            });

            btnNo.setOnClickListener(v -> {
                dialog.dismiss();
                // Khôi phục UI về trạng thái trước đó
                String currentLang = getCurrentLanguage();
                updateLanguageUI(currentLang.equals("vi"));
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing language dialog: " + e.getMessage());
        }
    }

    private void setNewLocale(String languageCode) {
        // Save language preference
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        settings.edit().putString("language", languageCode).apply();

        // Update locale for entire app
        App.setLocale(this);

        // Restart activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        // Apply animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void updateLanguageUI(boolean isVietnamese) {
        if (isVietnamese) {
            viText.setTextColor(getResources().getColor(android.R.color.white));
            enText.setTextColor(getResources().getColor(android.R.color.black));
            viButton.setBackgroundResource(R.drawable.btn_frame);
            enButton.setBackgroundResource(R.drawable.dialog_frame);
        } else {
            viText.setTextColor(getResources().getColor(android.R.color.black));
            enText.setTextColor(getResources().getColor(android.R.color.white));
            viButton.setBackgroundResource(R.drawable.dialog_frame);
            enButton.setBackgroundResource(R.drawable.btn_frame);
        }
    }

    private String getCurrentLanguage() {
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return settings.getString("language", "en");
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Kiểm tra xem email và password có hợp lệ không
            if (email.isEmpty() || password.isEmpty()) {
                // Hiển thị thông báo lỗi (nếu cần)
                Log.e(TAG, "Email or password is empty");
                showLoginStatusDialog(false, "Failed");
                return;
            }

            // Gọi hàm loginWithApi với email và password
            loginWithApi(email, password);
        });


        tvForgot.setOnClickListener(view -> {
            // Tạo Intent để mở ForgetPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgetpasswordActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Mở ForgetPasswordActivity
            startActivity(intent);
        });



        tvCreate.setOnClickListener(view -> {
            // Tạo Intent để mở RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Mở RegisterActivity
            startActivity(intent);
        });
    }


    private void showLoginStatusDialog(boolean isSuccess, String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status);

        // Set dialog window attributes
        Window window = dialog.getWindow();
        if (window != null) {
            // Make dialog full width
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

            // Set position and background
            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = statusBarHeight + extraPadding;;
            window.setAttributes(layoutParams);
           // window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvLoginStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvLoginStatus.setText("Login");
        tvStatusMessage.setText(isSuccess ? "Successful" : message);

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (isSuccess) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

    private void loginWithApi(String email, String password) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);
        LoginRequest loginRequest = new LoginRequest(email, password);

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken", refreshToken);
                    editor.putString("accessToken", accessToken);
                    editor.apply();

                    Log.d(TAG, "Login successful. Access Token: " + accessToken);
                    showLoginStatusDialog(true, "Successful");
                } else if (response.code() == 401) {
                    Log.e(TAG, "Login failed: Incorrect username or password.");
                    showLoginStatusDialog(false, "Failed");
                } else {
                    Log.e(TAG, "Login failed. Code: " + response.code());
                    showLoginStatusDialog(false, "Failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                showLoginStatusDialog(false, "Connection failed");
            }
        });
    }
}
