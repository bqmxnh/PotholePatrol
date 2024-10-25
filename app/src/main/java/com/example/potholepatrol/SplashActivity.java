package com.example.potholepatrol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the Get Started button
        Button getStartedButton = findViewById(R.id.getStartedButton);

        // Set click listener for the button
        getStartedButton.setOnClickListener(view -> {
            // Create intent to navigate to LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Optional: Add animation transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Optional: Close splash activity so user can't go back
            finish();
        });
    }
}
