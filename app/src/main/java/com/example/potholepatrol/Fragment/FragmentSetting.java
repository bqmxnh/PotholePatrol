package com.example.potholepatrol.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;

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
            // Add your logout logic here, like clearing user data or navigating to login screen
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
