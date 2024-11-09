package com.example.potholepatrol;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String WEB_CLIENT_ID = "378423130551-5knlk4hpg305j6hvbh4pkedrilno8b15.apps.googleusercontent.com";

    private CredentialManager credentialManager;
    private GoogleSignInClient mGoogleSignInClient;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;

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

            if (email.isEmpty() || password.isEmpty()) {
                Log.e(TAG, "Email or password is empty");
                Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            loginWithApi(email, password);
        });

        btnGoogle.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                startGoogleSignIn();
            } else {
                Toast.makeText(this, "Google Sign-In is not supported on your device version.", Toast.LENGTH_SHORT).show();
            }
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
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generateNonce() {
        byte[] nonce = new byte[32];
        new SecureRandom().nextBytes(nonce);
        return Base64.encodeToString(nonce, Base64.DEFAULT);
    }

    private void loginWithApi(String email, String password) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        LoginRequest loginRequest = new LoginRequest(email, password);

        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken", refreshToken);
                    editor.apply();

                    Log.d(TAG, "Login successful. Access Token: " + accessToken);
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Login failed. Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Unable to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
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

                Log.d(TAG, "Received ID Token from Google: " + idToken);

                sendGoogleTokenToServer(idToken);
            }
        } else {
            Log.e(TAG, "Unexpected credential type");
        }
    }

    private void sendGoogleTokenToServer(String idToken) {
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("credential", idToken);

        authService.googleSignIn(requestBody).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();

                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken", refreshToken);
                    editor.apply();

                    Log.d(TAG, "Google Sign-In successful. Access Token: " + accessToken);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Google Sign-In failed. Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Failed to send token to server: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Unable to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
