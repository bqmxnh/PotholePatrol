package com.example.potholepatrol.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.potholepatrol.R;

public class FragmentDashboard extends Fragment {

    private TextView textUsername;
    private ImageView iconUserImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho FragmentDashboard
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        // Thay đổi màu thanh trạng thái
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));

        // Khởi tạo textUsername
        textUsername = view.findViewById(R.id.text_username);
        iconUserImage = view.findViewById(R.id.icon_user_image);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String username = preferences.getString("user_name", "User");
        String photoUrl = preferences.getString("profile_photo_url", "");

        textUsername.setText(username);

        // Load profile image
        if (!photoUrl.isEmpty()) {
            loadProfileImage(photoUrl);
        }

        return view;
    }
    private void loadProfileImage(String photoUrl) {
        try {
            // Add Glide dependency in build.gradle if not already added:
            // implementation 'com.github.bumptech.glide:glide:4.12.0'
            Glide.with(requireContext())
                    .load(photoUrl)
                    .circleCrop() // Makes the image circular
                    .placeholder(R.mipmap.ic_user) // Show default image while loading
                    .error(R.mipmap.ic_user) // Show default image on error
                    .into(iconUserImage);
        } catch (Exception e) {
            Log.e("ProfileImage", "Error loading profile image: " + e.getMessage());
        }
    }
}
