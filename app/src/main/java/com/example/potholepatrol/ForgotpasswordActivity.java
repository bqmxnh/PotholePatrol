package com.example.potholepatrol;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

public class ForgotpasswordActivity extends AppCompatActivity {

    // Declare UI elements
    private ImageButton btnBack;
    private ImageView ivIllustration;
    private TextView tvForgetPassword;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);

        // Initialize UI elements
        btnBack = findViewById(R.id.btnBack);
        ivIllustration = findViewById(R.id.ivIllustration);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                finish(); // or use Intent
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle continue button click, e.g., perform validation and send reset link
            }
        });
    }
}
