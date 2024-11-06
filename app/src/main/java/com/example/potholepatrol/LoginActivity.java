package com.example.potholepatrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private CheckBox cbRemember;
    private MaterialButton btnLogin, btnGoogle;
    private TextView tvForgot, tvCreate;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các view
        initViews();

        // Cấu hình Google Sign-In
        configureGoogleSignIn();

        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgot = findViewById(R.id.tvForgot);
        tvCreate = findViewById(R.id.tvCreate);
    }

    private void configureGoogleSignIn() {
        // Cấu hình các tùy chọn đăng nhập của Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(view -> {
            // Xử lý sự kiện click vào nút Login
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // Thêm logic đăng nhập tại đây
        });

        btnGoogle.setOnClickListener(view -> {
            // Xử lý sự kiện click vào nút Google Sign-In
            signInWithGoogle();
        });

        tvForgot.setOnClickListener(view -> {
            // Xử lý sự kiện quên mật khẩu
        });

        tvCreate.setOnClickListener(view -> {
            // Xử lý sự kiện tạo tài khoản mới
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d("GoogleSignIn", "Dang nhap thanh cong: " + account.getEmail());

                    String userName = account.getDisplayName();

                    // Get profile image URL
                    String photoUrl = "";
                    if (account.getPhotoUrl() != null) {
                        photoUrl = account.getPhotoUrl().toString();
                    }

                    // Save user data to SharedPreferences
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user_name", userName);
                    editor.putString("profile_photo_url", photoUrl);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Dang nhap that bai, ma loi: " + e.getStatusCode());
            }
        }
    }
}
