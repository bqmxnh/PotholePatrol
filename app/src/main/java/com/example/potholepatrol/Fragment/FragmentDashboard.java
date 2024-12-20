package com.example.potholepatrol.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.Activity.AddPotholeActivity;
import com.example.potholepatrol.Dashboard.ActivityDashboardChart;
import com.example.potholepatrol.Dashboard.ActivityDashboardNotification;
import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.DailyChartRequest;
import com.example.potholepatrol.model.DailyChartResponse;
import com.example.potholepatrol.model.DashboardStatsResponse;
import com.example.potholepatrol.model.StatisticsData;
import com.example.potholepatrol.model.UserProfileResponse;
import com.google.gson.Gson;

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

        // Initialize views
        ImageView iconReport = view.findViewById(R.id.icon_report);
        ImageView iconNotification = view.findViewById(R.id.icon_notification);

        iconReport.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddPotholeActivity.class);
            startActivity(intent);
        });

        iconNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ActivityDashboardNotification.class);
            startActivity(intent);
        });

        // Initialize button chart and set click listener
        View btnChart = view.findViewById(R.id.btn_chart);
        btnChart.setOnClickListener(v -> {
            // Start ActivityDashboardChart
            Intent intent = new Intent(requireContext(), ActivityDashboardChart.class);
            startActivity(intent);
        });
        // Load data
        loadUserProfile();
        loadDashboardStats();
       loadDailyChart();


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
                    String rawResponse = new Gson().toJson(response.body());
                    Log.d("FragmentDashboard", "Raw JSON Response: " + rawResponse);
                    Log.d("FragmentDashboard", "Distance Traveled: " + stats.getData().getDistanceTraveled());
                    Log.d("FragmentDashboard", "Total: " + stats.getData().getTotal());
                    Log.d("FragmentDashboard", "Falls: " + stats.getData().getFalls());


                    DashboardStatsResponse.BySeverity severity = stats.getData().getBySeverity();


                    // Lưu vào SharedPreferences
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("DashboardStats", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("LowSeverity", severity.getLow());
                    editor.putInt("MediumSeverity", severity.getMedium());
                    editor.putInt("HighSeverity", severity.getHigh());
                    editor.apply();

                    textPotholes.setText(String.valueOf(stats.getData().getTotal()));
                    textFalls.setText(String.valueOf(stats.getData().getFalls()));
                    double distance = stats.getData().getDistanceTraveled();
                    textDistance.setText(String.format("%.1f km", distance));
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

    private String[] getLastWeekDates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        String endDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        String startDate = dateFormat.format(calendar.getTime());
        return new String[]{startDate, endDate};
    }

    private void loadDailyChart() {
        // Lấy token từ phương thức getAccessToken() (có thể là Bearer token)
        String token = "Bearer " + getAccessToken();

        // Lấy ngày bắt đầu và kết thúc từ phương thức getLastWeekDates()
        String[] dates = getLastWeekDates();
        String startDate = dates[0];
        String endDate = dates[1];

        Log.d("DailyChart", "startDate: " + startDate);
        Log.d("DailyChart", "endDate: " + endDate);

        // Khởi tạo service và gọi API
        AuthService authService = ApiClient.getClient().create(AuthService.class);

        // Gọi API với các tham số query (startDate và endDate)
        authService.getDailyChart(token, startDate, endDate).enqueue(new Callback<DailyChartResponse>() {
            @Override
            public void onResponse(Call<DailyChartResponse> call, Response<DailyChartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DailyChartResponse chartResponse = response.body();
                    Log.d("DailyChart", "Request URL: " + call.request().url().toString());
                    Log.d("DailyChart", "Raw JSON Response: " + new Gson().toJson(response.body()));
                    // Xử lý dữ liệu ở đây
                } else {
                    Log.e("DailyChart", "Failed to load daily chart. Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("DailyChart", "Error Body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DailyChartResponse> call, Throwable t) {
                Log.e("DailyChart", "Error: " + t.getMessage());
            }
        });

    }










}
