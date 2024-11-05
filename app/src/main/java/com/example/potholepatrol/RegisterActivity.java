package com.example.potholepatrol;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {


    private ImageButton btnBack;
    private ImageView ivIllustration;
    private TextView tvCreateAccount;
    private TextInputLayout tilUsername;
    private TextInputEditText etUsername;
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnSignUp;
    private TextView tvHave;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        btnBack = findViewById(R.id.btnBack);
        ivIllustration = findViewById(R.id.ivIllustration);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tilUsername = findViewById(R.id.tilUsernamee);
        etUsername = findViewById(R.id.etUsernamee);
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etPassword = findViewById(R.id.etPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTermss);
        btnSignUp = findViewById(R.id.btnSignUpp);
        tvHave = findViewById(R.id.tvHave);
        tvLogin = findViewById(R.id.tvLoginn);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                finish(); // Quay ve man hinh dang nhap
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle back to Login Screen
                finish(); // Quay ve man hinh dang nhap
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign up button click, e.g., perform validation and API call
            }
        });
    }
}
