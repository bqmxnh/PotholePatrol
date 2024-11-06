package com.example.potholepatrol;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
            // Xử lý sự kiện click vào nút Login (thêm logic đăng nhập tại đây)
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            btnGoogle.setOnClickListener(view -> startGoogleSignIn());
        }

        tvForgot.setOnClickListener(view -> {
            // Xử lý sự kiện quên mật khẩu
        });

        tvCreate.setOnClickListener(view -> {
            // Xử lý sự kiện tạo tài khoản mới
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
