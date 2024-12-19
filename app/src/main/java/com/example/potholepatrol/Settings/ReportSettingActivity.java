package com.example.potholepatrol.Settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;

public class ReportSettingActivity extends AppCompatActivity {

    private EditText editTextInput;
    private Button btnSave;
    private ImageView icBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_report);
        // Xử lý Status Bar
        setupStatusBar();
        // Khởi tạo các view
        editTextInput = findViewById(R.id.edit_text_input);
        btnSave = findViewById(R.id.btn_save);
        icBack = findViewById(R.id.ic_back);

        // Xử lý sự kiện nút "Save"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString().trim();
                if (inputText.isEmpty()) {
                    Toast.makeText(ReportSettingActivity.this, "Please enter a description!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lưu thông tin nhập liệu (có thể lưu vào cơ sở dữ liệu hoặc API)
                    Toast.makeText(ReportSettingActivity.this, "Report saved successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        icBack.setOnClickListener(v -> finish());

    }
    private void setupStatusBar() {
        Window window = getWindow();

        // Làm trong suốt status bar và kích hoạt full-screen mode
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
