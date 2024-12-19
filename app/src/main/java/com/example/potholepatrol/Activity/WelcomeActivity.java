package com.example.potholepatrol.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Làm cho thanh trạng thái và thanh điều hướng trong suốt
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_welcome);

        // Tìm các view
        ImageView backgroundImage = findViewById(R.id.backgroundImage);
        View shadowOverlay = findViewById(R.id.shadowOverlay);
        TextView titleText = findViewById(R.id.titleText);
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Tải các animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation shadowFadeIn = AnimationUtils.loadAnimation(this, R.anim.shadow_fade_in);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Animation buttonFadeIn = AnimationUtils.loadAnimation(this, R.anim.button_fade_in);

        // Áp dụng animation
        backgroundImage.startAnimation(fadeIn);
        shadowOverlay.startAnimation(shadowFadeIn);
        titleText.startAnimation(bounce);
        getStartedButton.startAnimation(buttonFadeIn);

        // Thiết lập click listener cho nút
        getStartedButton.setOnClickListener(view -> {
            // Tạo intent để chuyển đến LoginActivity
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);

            // Tùy chọn: Thêm chuyển đổi hiệu ứng animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Tùy chọn: Đóng WelcomeActivity để người dùng không thể quay lại
            finish();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }
}
