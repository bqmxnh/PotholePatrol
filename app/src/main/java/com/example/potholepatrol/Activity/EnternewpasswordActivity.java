package com.example.potholepatrol.Activity;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.model.NewPasswordRequest;
import com.example.potholepatrol.model.NewPasswordResponse;

public class EnternewpasswordActivity extends AppCompatActivity {


    private ImageButton btnBack;
    private ImageView ivIllustration;
    private TextView tvResetPassword;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private MaterialButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enternewpassword);


        btnBack = findViewById(R.id.btnBack);
        ivIllustration = findViewById(R.id.ivIllustration);
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnConfirm = findViewById(R.id.btnConfirm);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (password.isEmpty() || confirmPassword.isEmpty()) {

                    Toast.makeText(EnternewpasswordActivity.this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (password.equals(confirmPassword)) {



                    Intent intent1 = getIntent();
                    String email = intent1.getStringExtra("email");
                    String resetToken = intent1.getStringExtra("resetToken");
                    Log.d("EnternewpasswordActivity", "Email: " + email);
                    Log.d("EnternewpasswordActivity", "ResetToken: " + resetToken);
                    Log.d("EnternewpasswordActivity", "password: " + password);
                    newPasswordWithApi(email,resetToken,password);

                } else {

                    Toast.makeText(EnternewpasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }

    private void newPasswordWithApi(String email, String resetToken, String newPassword) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);


        NewPasswordRequest newPasswordRequest = new NewPasswordRequest(email, resetToken, newPassword);

        // Call API
        authService.newPassword(newPasswordRequest).enqueue(new retrofit2.Callback<NewPasswordResponse>() {
            @Override
            public void onResponse(retrofit2.Call<NewPasswordResponse> call, retrofit2.Response<NewPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewPasswordResponse newPasswordResponse = response.body();
                    String message = newPasswordResponse.getMessage();

                    Log.d(TAG, "Password update successful: " + message);
                    Toast.makeText(EnternewpasswordActivity.this, "Password update successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EnternewpasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Log.e(TAG, "Password update failed. Code: " + response.code() + ", Error: " + response.message());
                    Toast.makeText(EnternewpasswordActivity.this, "Password update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(retrofit2.Call<NewPasswordResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(EnternewpasswordActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
