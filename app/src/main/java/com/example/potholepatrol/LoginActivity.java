package com.example.potholepatrol;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;





import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.example.potholepatrol.ForgetpasswordActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.LoginRequest;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private TextView tvForgot, tvCreate, statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các view
        initViews();

        // Cấu hình Google Sign-In
        setupGoogleSignIn();



        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgot = findViewById(R.id.tvForgot);
        tvCreate = findViewById(R.id.tvCreate);
        //statusText = findViewById(R.id.statusText);
    }

    private void setupGoogleSignIn() {
        String webClientId = getString(R.string.google_auth_web_client_id);

        // Khởi tạo SignInClient
        oneTapClient = Identity.getSignInClient(this);

        // Cấu hình yêu cầu đăng nhập
        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(webClientId)
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .setAutoSelectEnabled(false)
                .build();
    }


    private void setupClickListeners() {
        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Kiểm tra xem email và password có hợp lệ không
            if (email.isEmpty() || password.isEmpty()) {
                // Hiển thị thông báo lỗi (nếu cần)
                Log.e(TAG, "Email or password is empty");
                return;
            }

            // Gọi hàm loginWithApi với email và password
            loginWithApi(email, password);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            btnGoogle.setOnClickListener(view -> startGoogleSignIn());
        }

        tvForgot.setOnClickListener(view -> {
            // Tạo Intent để mở ForgetPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgetpasswordActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Mở ForgetPasswordActivity
            startActivity(intent);
        });



        tvCreate.setOnClickListener(view -> {
            // Tạo Intent để mở RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Mở RegisterActivity
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(),
                                1001,
                                null,
                                0,
                                0,
                                0
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting Google Sign-In: " + e.getMessage(), e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Google Sign-In failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private String generateNonce() {
        byte[] nonce = new byte[32]; // Tạo mảng byte 32 ký tự
        new SecureRandom().nextBytes(nonce); // Sinh giá trị ngẫu nhiên
        return Base64.encodeToString(nonce, Base64.DEFAULT).trim(); // Chuyển thành chuỗi Base64
    }


    private void loginWithApi(String email, String password) {
        // Khởi tạo AuthService từ ApiClient
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        // Tạo đối tượng LoginRequest để chứa email và password
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Gọi API đăng nhập
        authService.login(loginRequest).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi đăng nhập thành công
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();

                    // Lưu refreshToken vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken", refreshToken);
                    String email = etEmail.getText().toString().trim();
                    editor.apply();

                    Log.d(TAG, "Login successful. Access Token: " + accessToken);
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Chuyển đến MainActivity sau khi đăng nhập thành công
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (response.code() == 401) {
                    Log.e(TAG, "Login failed: Incorrect username or password.");
                    Toast.makeText(LoginActivity.this, "Login failed: Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG, "Login failed. Code: " + response.code());

                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                // Xử lý lỗi khi gọi API thất bại
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();

                if (idToken != null) {
                    Log.d(TAG, "Google ID Token: " + idToken);
                    sendGoogleTokenToServer(idToken);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Error retrieving Google Sign-In credential: " + e.getMessage(), e);
            }
        }
    }

    private void sendGoogleTokenToServer(String idToken) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("credential", idToken);

        authService.googleSignIn(requestBody).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken", loginResponse.getRefreshToken());
                    editor.apply();

                    Log.d(TAG, "Google Sign-In successful. Access Token: " + loginResponse.getAccessToken());

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Google Sign-In failed. Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to send token to server: " + t.getMessage());
            }
        });
    }
}