package com.example.potholepatrol.Dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;

public class ActivityDashboardChart extends AppCompatActivity {
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_chart);

        // Change the color of the status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));
        backButton = findViewById(R.id.ic_back);
        setupClickListeners();

    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());


    }
}