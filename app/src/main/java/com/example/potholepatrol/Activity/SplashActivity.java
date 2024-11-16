package com.example.potholepatrol.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Làm cho status bar và thanh điều hướng có màu trắng
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        window.setStatusBarColor(getResources().getColor(android.R.color.white));
        window.setNavigationBarColor(getResources().getColor(android.R.color.white));

        setContentView(R.layout.activity_splash);

        // Tìm ImageView biểu tượng
        ImageView logoIcon = findViewById(R.id.logoIcon);

        // Tải và áp dụng các hoạt ảnh
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        logoIcon.startAnimation(fadeIn);
        logoIcon.startAnimation(zoomIn);

        // Hiển thị Splash Screen trong 2 giây (2000ms)
        new Handler().postDelayed(() -> {
            // Chuyển đến WelcomeActivity
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish(); // Đóng SplashActivity để không quay lại
        }, 2000); // Thời gian chờ 2000ms (2 giây)
    }
}
