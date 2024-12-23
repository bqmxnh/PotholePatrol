package com.example.potholepatrol.Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.example.potholepatrol.model.ForgetPasswordResponse;

import retrofit2.Call;


public class ForgetpasswordActivity extends AppCompatActivity {

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

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnContinue.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            requestOTP(email);
//            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("email", email);
//            editor.apply();
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }

    private void requestOTP(String email) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(email);

        authService.forgetPassword(forgetPasswordRequest).enqueue(new retrofit2.Callback<ForgetPasswordResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ForgetPasswordResponse> call, retrofit2.Response<ForgetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "OTP sent successfully.");
                    Toast.makeText(ForgetpasswordActivity.this, "OTP sent successfully. Check your email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgetpasswordActivity.this, EnterotpActivity.class );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String email = etEmail.getText().toString().trim();
                    intent.putExtra("email", email);
                    startActivity(intent);

                } else {
                    if (response.code() == 500) {
                        Log.e(TAG, "User is not registered.");
                        Toast.makeText(ForgetpasswordActivity.this, "User is not registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to send OTP. Code: " + response.code());
                        Toast.makeText(ForgetpasswordActivity.this, "Failed to send OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(ForgetpasswordActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }






}
