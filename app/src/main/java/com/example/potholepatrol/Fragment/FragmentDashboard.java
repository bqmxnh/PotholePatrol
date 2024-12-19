package com.example.potholepatrol.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.DashboardStatsResponse;
import com.example.potholepatrol.model.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDashboard extends Fragment {

    private TextView textUsername, textPotholes, textDistance, textFalls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for FragmentDashboard
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        // Change the color of the status bar
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));

        // Initialize views
        textUsername = view.findViewById(R.id.text_username);
        textPotholes = view.findViewById(R.id.text_potholes_data);
        textDistance = view.findViewById(R.id.text_distance_data);
        textFalls = view.findViewById(R.id.text_falls_data);

        // Load data
        loadUserProfile();
        loadDashboardStats();

        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(App.wrap(context));
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", ""); // Trả về token hoặc chuỗi rỗng nếu không có
    }

    private void loadUserProfile() {
        String token = "Bearer " + getAccessToken();

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.getUserProfile(token).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    // Hiển thị tên người dùng hoặc thông báo nếu null
                    String name = profile.getUsername() != null ? profile.getUsername() : "Please update your name";
                    textUsername.setText(name);
                } else {
                    Log.e("FragmentDashboard", "Failed to load user profile. Code: " + response.code());
                    textUsername.setText("Please update your name");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e("FragmentDashboard", "Error loading user profile: " + t.getMessage());
                textUsername.setText("Please update your name");
            }
        });
    }

    private void loadDashboardStats() {
        String token = "Bearer " + getAccessToken();

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        authService.getDashboardStats(token).enqueue(new Callback<DashboardStatsResponse>() {
            @Override
            public void onResponse(Call<DashboardStatsResponse> call, Response<DashboardStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStatsResponse stats = response.body();

                    textPotholes.setText(String.valueOf(stats.getData().getTotal()));
                    textFalls.setText(String.valueOf(stats.getData().getFalls()));
                    textDistance.setText(stats.getData().getDistanceTraveled() + " km");
                } else {
                    Log.e("FragmentDashboard", "Failed to load dashboard stats. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DashboardStatsResponse> call, Throwable t) {
                Log.e("FragmentDashboard", "Error loading dashboard stats: " + t.getMessage());
            }
        });
    }

}
