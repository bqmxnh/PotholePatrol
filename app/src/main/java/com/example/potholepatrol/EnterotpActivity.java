package com.example.potholepatrol;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

public class EnterotpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enterotp);




        // Handle Back Button Action
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();  // Go back to the previous screen
        });

        // Handle Reset Button Action
        findViewById(R.id.btnReset).setOnClickListener(v -> {
            // Extract OTP from the input fields
            String otp = getOTP();
            if (otp.length() == 6) {
                // Process OTP (e.g., validate it or send to backend)
                Toast.makeText(this, "OTP: " + otp, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get OTP from the EditText fields
    private String getOTP() {
        StringBuilder otp = new StringBuilder();
        otp.append(((EditText) findViewById(R.id.otp1)).getText().toString());
        otp.append(((EditText) findViewById(R.id.otp2)).getText().toString());
        otp.append(((EditText) findViewById(R.id.otp3)).getText().toString());
        otp.append(((EditText) findViewById(R.id.otp4)).getText().toString());
        otp.append(((EditText) findViewById(R.id.otp5)).getText().toString());
        otp.append(((EditText) findViewById(R.id.otp6)).getText().toString());
        return otp.toString();
    }
}
