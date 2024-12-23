package com.example.potholepatrol.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.LoginRequest;
import com.example.potholepatrol.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        window.setStatusBarColor(getResources().getColor(android.R.color.white));
        window.setNavigationBarColor(getResources().getColor(android.R.color.white));

        setContentView(R.layout.activity_splash);

        ImageView logoIcon = findViewById(R.id.logoIcon);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        logoIcon.startAnimation(fadeIn);
        logoIcon.startAnimation(zoomIn);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password= sharedPreferences.getString("password", "");
        boolean cbRemember = sharedPreferences.getBoolean("remember_me", false);
        if(email!="" && password!="" && cbRemember!=false)
        {
            loginWithApi(email, password);
        }

        else
        {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }, 2000);
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
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

                    Log.d("AutoLogin", "Login successful. Access Token: " + accessToken);
                    showLoginStatusDialog(true, "Successful");
                } else if (response.code() == 401) {
                    Log.e("AutoLogin", "Login failed: Incorrect username or password.");
                    showLoginStatusDialog(false, "Failed");
                } else {
                    Log.e("AutoLogin", "Login failed. Code: " + response.code());
                    showLoginStatusDialog(false, "Failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("AutoLogin", "API call failed: " + t.getMessage());
                showLoginStatusDialog(false, "Connection failed");
            }
        });
    }

    private void showLoginStatusDialog(boolean isSuccess, String message) {
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
        TextView tvLoginStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvLoginStatus.setText("Login");
        tvStatusMessage.setText(isSuccess ? "Successful" : message);

        // Lắng nghe sự kiện khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> {
            if (isSuccess) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
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

}
