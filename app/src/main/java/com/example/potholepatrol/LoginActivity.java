package com.example.potholepatrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Callback;
import retrofit2.Response;




import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.example.potholepatrol.ForgetpasswordActivity;

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



public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String WEB_CLIENT_ID = "854216792154-kq826a1ugc767o9satue0tp9457lr4h1.apps.googleusercontent.com";

    private CredentialManager credentialManager;
    private GoogleSignInClient mGoogleSignInClient;

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

        // Thiết lập Credential Manager
        credentialManager = CredentialManager.create(this);

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setNonce(generateNonce())
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                getMainExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Error getting credential", e);
                        //statusText.setText("Sign in failed: " + e.getMessage());
                    }
                });
    }

    private String generateNonce() {
        byte[] nonce = new byte[32];
        new SecureRandom().nextBytes(nonce);
        return Base64.encodeToString(nonce, Base64.DEFAULT);
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
                    editor.apply();

                    Log.d(TAG, "Login successful. Access Token: " + accessToken);

                    // Chuyển đến MainActivity sau khi đăng nhập thành công
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
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





    private void handleSignIn(GetCredentialResponse result) {
        if (result == null || result.getCredential() == null) {
            Log.e(TAG, "No credential received");
            return;
        }

        if (result.getCredential() instanceof CustomCredential) {
            CustomCredential credential = (CustomCredential) result.getCredential();

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());

                String idToken = googleIdTokenCredential.getIdToken();
                String displayName = googleIdTokenCredential.getDisplayName();
                String profilePictureUri = googleIdTokenCredential.getProfilePictureUri() != null ?
                        googleIdTokenCredential.getProfilePictureUri().toString() : "";

                String userInfo = String.format("Signed in successfully!\nName: %s\nToken: %s", displayName, idToken);
                Log.d(TAG, "ID Token: " + idToken);

                // Chuyển sang MainActivity sau khi đăng nhập thành công
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng LoginActivity để quay lại sẽ không trở lại màn hình đăng nhập nữa
            }
        } else {
            Log.e(TAG, "Unexpected credential type");
        }
    }

}
