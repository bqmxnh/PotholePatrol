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
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;
import com.example.potholepatrol.Settings.EditSettingActivity;
import com.example.potholepatrol.Settings.NotificationSettingActivity;
import com.example.potholepatrol.Settings.PersonalizationSettingActivity;
import com.example.potholepatrol.Settings.PrivacySettingActivity;
import com.example.potholepatrol.Settings.ReportSettingActivity;
import com.example.potholepatrol.Settings.TermSettingActivity;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.Activity.LoginActivity;
import com.example.potholepatrol.model.UserProfileResponse;

import java.util.HashMap;
import java.util.Map;

public class FragmentSetting extends Fragment {
    private int originalStatusBarColor;
    private int originalSystemUiVisibility;
    private TextView textUserName, textUserEmail;

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

        // Initialize TextViews for user info
        textUserName = view.findViewById(R.id.text_user_name);
        textUserEmail = view.findViewById(R.id.text_user_email);

        // Load user profile information
        loadUserProfile();

        // Setting up navigation for all setting options
        setupSettingOptions(view);

        return view;
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    private void loadUserProfile() {
        String token = "Bearer " + getAccessToken();

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.getUserProfile(token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    String name = profile.getUsername() != null ? profile.getUsername() : "Please update your name";
                    String email = profile.getEmail() != null ? profile.getEmail() : "Please update your email";

                    textUserName.setText(name);
                    textUserEmail.setText(email);
                } else {
                    Log.e(TAG, "Failed to load user profile. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                textUserName.setText("Please update your name");
                textUserEmail.setText("Please update your email");
                Log.e(TAG, "Error loading user profile: " + t.getMessage());
            }
        });
    }

    private void setupSettingOptions(View view) {
        // Edit Profile Option
        View editOption = view.findViewById(R.id.option_edit_profile);
        editOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditSettingActivity.class);
            startActivity(intent);
        });

        // Personalization Option
        View personalizationOption = view.findViewById(R.id.option_personalization);
        personalizationOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalizationSettingActivity.class);
            startActivity(intent);
        });

        // Notification Option
        View notificationOption = view.findViewById(R.id.option_notifications);
        notificationOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationSettingActivity.class);
            startActivity(intent);
        });

        // Privacy Option
        View privacyOption = view.findViewById(R.id.option_privacy);
        privacyOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PrivacySettingActivity.class);
            startActivity(intent);
        });

        // Report Problem Option
        View reportOption = view.findViewById(R.id.option_report_problem);
        reportOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportSettingActivity.class);
            startActivity(intent);
        });

        // Terms and Policies Option
        View termOption = view.findViewById(R.id.option_terms_policies);
        termOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TermSettingActivity.class);
            startActivity(intent);
        });

        // Logout Option
        View logoutFrame = view.findViewById(R.id.option_logout);
        logoutFrame.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(originalStatusBarColor);
        window.getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.setting_dialog_logout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        LinearLayout btnLogout = dialogView.findViewById(R.id.btn_logout);
        LinearLayout btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String refreshToken = sharedPreferences.getString("refreshToken", null);
            logoutWithApi(refreshToken);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void logoutWithApi(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            Log.e(TAG, "Refresh token is missing.");
            return;
        }

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        authService.logout(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Logout successful.");
                    clearUserSession();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    requireActivity().startActivity(intent);
                    requireActivity().finish();
                } else {
                    Log.e(TAG, "Logout failed. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void clearUserSession() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}