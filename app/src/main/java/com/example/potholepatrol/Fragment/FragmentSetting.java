package com.example.potholepatrol.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        // Setup language buttons
        setupLanguageButtons(view);

        // Setting up navigation for all setting options
        setupSettingOptions(view);

        return view;
    }

    private void setupLanguageButtons(View view) {
        // Find language buttons and their text views
        View viButton = view.findViewById(R.id.vi_button);
        View enButton = view.findViewById(R.id.en_button);
        TextView viText = view.findViewById(R.id.text_vi);
        TextView enText = view.findViewById(R.id.text_en);

        // Set click listeners
        viButton.setOnClickListener(v -> {
            setNewLocale("vi");
            updateLanguageUI(viText, enText, true);
        });

        enButton.setOnClickListener(v -> {
            setNewLocale("en");
            updateLanguageUI(viText, enText, false);
        });

        // Set initial state
        String currentLang = getCurrentLanguage();
        updateLanguageUI(viText, enText, currentLang.equals("vi"));
    }

    private void updateLanguageUI(TextView viText, TextView enText, boolean isVietnamese) {
        if (isVietnamese) {
            viText.setTextColor(getResources().getColor(android.R.color.white));
            enText.setTextColor(getResources().getColor(android.R.color.black));
            ((View) viText.getParent()).setBackgroundResource(R.drawable.btn_frame);
            ((View) enText.getParent()).setBackgroundResource(R.drawable.dialog_frame);
        } else {
            viText.setTextColor(getResources().getColor(android.R.color.black));
            enText.setTextColor(getResources().getColor(android.R.color.white));
            ((View) viText.getParent()).setBackgroundResource(R.drawable.dialog_frame);
            ((View) enText.getParent()).setBackgroundResource(R.drawable.btn_frame);
        }
    }

    private void setNewLocale(String languageCode) {
        try {
            // Inflate the custom dialog view
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_change_language, null); // Replace with the correct dialog layout filename

            // Find views in the dialog
            TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
            TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
            Button btnYes = dialogView.findViewById(R.id.btnYes);
            Button btnNo = dialogView.findViewById(R.id.btnNo);

            // Customize dialog title and message
            dialogTitle.setText(getString(R.string.language_change_title)); // Define this in strings.xml
            dialogMessage.setText(getString(R.string.language_change_message)); // Define this in strings.xml

            // Create and show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            // Set click listeners for the buttons
            btnYes.setOnClickListener(v -> {
                dialog.dismiss();

                // Save language preference
                SharedPreferences settings = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                settings.edit().putString("language", languageCode).apply();

                // Update locale
                Locale locale = new Locale(languageCode);
                Locale.setDefault(locale);

                Resources resources = requireContext().getResources();
                Configuration config = new Configuration(resources.getConfiguration());
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());

                // Restart the activity
                requireActivity().recreate();
            });

            btnNo.setOnClickListener(v -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Optional: Set background to transparent
            }

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error setting locale: " + e.getMessage());
        }
    }


    private String getCurrentLanguage() {
        SharedPreferences settings = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return settings.getString("language", "en");
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