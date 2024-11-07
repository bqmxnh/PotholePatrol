package com.example.potholepatrol;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.google.android.gms.common.api.Response;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.LoginRequest;
import com.example.potholepatrol.model.ForgetPasswordResponse;
import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.example.potholepatrol.RegisterActivity;

import javax.security.auth.callback.Callback;

import retrofit2.Call;


public class ForgetpasswordActivity extends AppCompatActivity {

    // Khai báo các biến
    private ImageButton btnBack;
    private TextView tvForgetPassword;
    private TextInputEditText etEmail;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        // Liên kết các view với biến
        btnBack = findViewById(R.id.btnBack);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);

        // Thiết lập sự kiện cho nút Back
        btnBack.setOnClickListener(v -> {
            finish(); //quay lại màn login
        });

        // Thiết lập sự kiện cho nút Continue
        btnContinue.setOnClickListener(v -> {
            // Kiểm tra và xử lý dữ liệu email nhập vào
            String email = etEmail.getText().toString().trim();
            requestOTP(email);
//            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("email", email);
//            editor.apply();
        });


    }

    private void requestOTP(String email) {
        // Khởi tạo AuthService từ ApiClient
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        // Tạo đối tượng ForgetPasswordRequest với email
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(email);

        // Gọi API để gửi OTP
        authService.forgetPassword(forgetPasswordRequest).enqueue(new retrofit2.Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ForgetPasswordResponse> call, retrofit2.Response<ForgetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi gửi OTP thành công
                    Log.d(TAG, "OTP sent successfully.");
                    // Hiển thị thông báo gửi OTP thành công
                    Toast.makeText(ForgetpasswordActivity.this, "OTP sent successfully. Check your email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgetpasswordActivity.this, EnterotpActivity.class );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String email = etEmail.getText().toString().trim();
                    intent.putExtra("email", email);
                    startActivity(intent);

                } else {
                    // Kiểm tra mã lỗi 500 (User is not registered)
                    if (response.code() == 500) {
                        Log.e(TAG, "User is not registered.");
                        Toast.makeText(ForgetpasswordActivity.this, "User is not registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý khi API trả về lỗi khác
                        Log.e(TAG, "Failed to send OTP. Code: " + response.code());
                        // Hiển thị lỗi nếu không gửi OTP thành công
                        Toast.makeText(ForgetpasswordActivity.this, "Failed to send OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                // Xử lý khi có lỗi kết nối API
                Log.e(TAG, "API call failed: " + t.getMessage());
                // Hiển thị lỗi khi gọi API thất bại
                Toast.makeText(ForgetpasswordActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }






}
