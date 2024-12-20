package com.example.potholepatrol.Settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.ReportRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportSettingActivity extends AppCompatActivity {

    private EditText editTextInput;
    private Button btnSave;
    private ImageView icBack;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_report);

        authService = ApiClient.getClient().create(AuthService.class);

        setupStatusBar();
        editTextInput = findViewById(R.id.edit_text_input);
        btnSave = findViewById(R.id.btn_save);
        icBack = findViewById(R.id.ic_back);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString().trim();
                if (inputText.isEmpty()) {
                    showStatusDialog(false, "Please enter a description!");
                } else {
                    sendReport(inputText);
                }
            }
        });

        icBack.setOnClickListener(v -> finish());
    }
    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", ""); // Trả về token hoặc chuỗi rỗng nếu không có
    }
    private void sendReport(String description) {
        String token = "Bearer " + getAccessToken();

        ReportRequest reportRequest = new ReportRequest(description);

        Log.d("ReportAPI", "Sending report with description: " + description);
        Log.d("ReportAPI", "Using token: " + token);

        authService.sendReport(token, reportRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("ReportAPI", "Response code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d("ReportAPI", "Report sent successfully");
                    showStatusDialog(true, "Report sent successfully!");
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "No error body";
                        Log.d("ReportAPI", "Error response: " + errorBody);
                        Log.d("ReportAPI", "Error code: " + response.code());
                    } catch (IOException e) {
                        Log.e("ReportAPI", "Error reading error body", e);
                    }
                    showStatusDialog(false, "Failed to send report. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ReportAPI", "Network error", t);
                Log.d("ReportAPI", "Error message: " + t.getMessage());
                showStatusDialog(false, "Network error. Please check your connection.");
            }
        });
    }

    private void showStatusDialog(boolean isSuccess, String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status);

        // Set dialog window attributes
        Window window = dialog.getWindow();
        if (window != null) {
            // Make dialog full width
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = 0;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize dialog views
        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        // Set dialog content
        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvStatus.setText("Privacy Settings");
        tvStatusMessage.setText(message);

        dialog.show();

        // Auto dismiss after 2 seconds and finish activity if success
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                if (isSuccess) {
                    finish();
                }
            }
        }, 2000);
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }
}