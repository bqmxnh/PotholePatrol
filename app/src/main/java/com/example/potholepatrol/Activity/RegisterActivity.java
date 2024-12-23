package com.example.potholepatrol.Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.RegisterRequest;
import com.example.potholepatrol.model.RegisterResponse;

public class RegisterActivity extends AppCompatActivity {

    // Khai báo các biến
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnSignUp;
    private TextView tvHave, tvLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsernamee);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tilUsername = findViewById(R.id.tilUsernamee);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        cbTerms = findViewById(R.id.cbTermss);
        btnSignUp = findViewById(R.id.btnSignUpp);

        tvHave = findViewById(R.id.tvHave);
        tvLogin = findViewById(R.id.tvLoginn);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                // Hiển thị thông báo lỗi nếu mật khẩu không trùng
                Toast.makeText(RegisterActivity.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
            } else if (!cbTerms.isChecked()) {
                // Hiển thị thông báo nếu người dùng chưa đồng ý với Điều khoản và Chính sách
                Toast.makeText(RegisterActivity.this, "Please agree to the Terms and Policies.", Toast.LENGTH_SHORT).show();
            } else if (etPassword.length()<4) {
                Toast.makeText(RegisterActivity.this, "Password length must be at least 4 characters long.", Toast.LENGTH_SHORT).show();
            } else {
                // Tiến hành đăng ký nếu mật khẩu trùng khớp và đã đồng ý với điều khoản
                registerWithApi(username, email, password);
            }
        });



        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }
    private void registerWithApi(String username, String email, String password) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        authService.register(registerRequest).enqueue(new retrofit2.Callback<RegisterResponse>() {
            @Override

            public void onResponse(retrofit2.Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    String message = registerResponse.getMessage();

                    Log.d(TAG, "Registration successful: " + message);
                    Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (response.code() == 409) {
                        Log.e(TAG, "Registration failed: Email is already in use");
                        Toast.makeText(RegisterActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Registration failed. Code: " + response.code());
                        Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(@NonNull retrofit2.Call<RegisterResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());

                Toast.makeText(RegisterActivity.this, "Registration failed. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
