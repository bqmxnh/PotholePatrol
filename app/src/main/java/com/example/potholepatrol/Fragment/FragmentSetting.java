package com.example.potholepatrol.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.potholepatrol.R;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.model.LogoutRequest;
import com.example.potholepatrol.model.LogoutResponse;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.LoginActivity;



public class FragmentSetting extends Fragment {
    private int originalStatusBarColor;
    private int originalSystemUiVisibility;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = requireActivity().getWindow();

        // Save original status bar color and system UI visibility
        originalStatusBarColor = window.getStatusBarColor();
        originalSystemUiVisibility = window.getDecorView().getSystemUiVisibility();

        // Make status bar transparent and set full-screen mode
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        View view = inflater.inflate(R.layout.activity_setting, container, false);

        // Find the logout button
        View logoutFrame = view.findViewById(R.id.option_logout);
        logoutFrame.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Restore original status bar color and system UI visibility
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(originalStatusBarColor);
        window.getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
    }

    private void showLogoutDialog() {
        // Create and show the logout confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.setting_dialog_logout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Set the dialog window background to transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up dialog buttons
        LinearLayout btnLogout = dialogView.findViewById(R.id.btn_logout);
        LinearLayout btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnLogout.setOnClickListener(v -> {
            // Perform logout action
            dialog.dismiss();
            // Lấy refreshToken từ SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String refreshToken = sharedPreferences.getString("refreshToken", null);
            logoutWithApi(refreshToken);

        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Cập nhật API để gọi logout
    private void logoutWithApi(String refreshToken) {
        // Khởi tạo AuthService từ ApiClient
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        // Tạo đối tượng LogoutRequest với refreshToken
        LogoutRequest logoutRequest = new LogoutRequest(refreshToken);

        // Gọi API đăng xuất
        authService.logout(logoutRequest).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful()) {
                    // Xử lý khi đăng xuất thành công
                    Log.d(TAG, "Logout successful.");


                    clearUserSession();

                    // Chuyển đến màn hình đăng nhập




                } else {
                    // Xử lý khi API trả về lỗi
                    Log.e(TAG, "Logout failed. Code: " + response.code());

                }
            }

            private void clearUserSession() {
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                // Xử lý khi có lỗi kết nối API
                Log.e(TAG, "API call failed: " + t.getMessage());

            }
        });
    }

}
