package com.example.potholepatrol;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.VerifyOTPRequest;
import com.example.potholepatrol.model.VerifyOTPResponse;
import com.example.potholepatrol.LoginActivity;
import android.content.SharedPreferences;



public class EnterotpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterotp);

        // Handle Back Button Action
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();  // Go back to the previous screen
        });


        findViewById(R.id.btnReset).setOnClickListener(v -> {
            String otp = getOTP().trim();
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");


            if (otp.length() != 6) {
                Toast.makeText(this, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ResetPassword", "Email: " + email);
            Log.d("ResetPassword", "OTP: " + otp);

            // call the API
            resetPasswordWithApi(email, otp);

        });


        setupOTPTextWatchers();
    }


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


    private void setupOTPTextWatchers() {
        EditText otp1 = findViewById(R.id.otp1);
        EditText otp2 = findViewById(R.id.otp2);
        EditText otp3 = findViewById(R.id.otp3);
        EditText otp4 = findViewById(R.id.otp4);
        EditText otp5 = findViewById(R.id.otp5);
        EditText otp6 = findViewById(R.id.otp6);

        otp1.addTextChangedListener(new OTPTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new OTPTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new OTPTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new OTPTextWatcher(otp4, otp5));
        otp5.addTextChangedListener(new OTPTextWatcher(otp5, otp6));
        otp6.addTextChangedListener(new OTPTextWatcher(otp6, null));
    }


    private class OTPTextWatcher implements TextWatcher {
        private EditText currentEditText;
        private EditText nextEditText;

        OTPTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            // Khi nhập một ký tự, tự động chuyển qua trường kế tiếp
            if (editable.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
            // Khi xóa ký tự
            else if (editable.length() == 0) {
                // Nếu đang ở ô đầu tiên thì không di chuyển
                if (currentEditText.getId() != R.id.otp1) {
                    EditText previousEditText = findViewById(getPreviousEditTextId());
                    previousEditText.requestFocus();
                }
            }
        }

        private int getPreviousEditTextId() {
            if (currentEditText.getId() == R.id.otp2) {
                return R.id.otp1;
            } else if (currentEditText.getId() == R.id.otp3) {
                return R.id.otp2;
            } else if (currentEditText.getId() == R.id.otp4) {
                return R.id.otp3;
            } else if (currentEditText.getId() == R.id.otp5) {
                return R.id.otp4;
            } else if (currentEditText.getId() == R.id.otp6) {
                return R.id.otp5;
            }
            return -1;
        }
    }

    private void resetPasswordWithApi(String email, String otp) {

        AuthService authService = ApiClient.getClient().create(AuthService.class);

        VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest(email, otp);

        // gọi API
        authService.verifyOTP(verifyOTPRequest).enqueue(new retrofit2.Callback<VerifyOTPResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VerifyOTPResponse> call, retrofit2.Response<VerifyOTPResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    VerifyOTPResponse verifyOTPResponse = response.body();
                    String resetToken = verifyOTPResponse.getResetToken();

                    Log.d(TAG, "Password reset successful. Reset Token: " + resetToken);
                    Toast.makeText(EnterotpActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    Intent intent1 = getIntent();
                    String email = intent1.getStringExtra("email");
                    //  reset password screen
                    Intent intent = new Intent(EnterotpActivity.this, EnternewpasswordActivity.class);
                    intent.putExtra("resetToken", resetToken);
                    intent.putExtra("email", email);
                    Log.d("EnternewpasswordActivity", "Email: " + email);
                    Log.d("EnternewpasswordActivity", "ResetToken: " + resetToken);
                    startActivity(intent);
                    finish();

                } else {

                    Log.e(TAG, "Password reset failed. Code: " + response.code());
                    Toast.makeText(EnterotpActivity.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<VerifyOTPResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                // Handle error when API call fails
                Toast.makeText(EnterotpActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
