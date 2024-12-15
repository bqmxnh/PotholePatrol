package com.example.potholepatrol.Settings;
import android.os.Bundle;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;

public class TermSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_terms_policies);
        // Set up back button
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
}