package com.example.potholepatrol.Dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.potholepatrol.Adapter.NotificationAdapter;
import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.DeleteNotificationRequest;
import com.example.potholepatrol.model.NotificationItem;
import com.example.potholepatrol.model.NotificationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityDashboardNotification extends AppCompatActivity implements NotificationAdapter.OnNotificationActionListener {

    private ListView listViewNotifications;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationsList;
    private AuthService authService;
    private Handler handler;
    private Runnable notificationChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_notification);

        ImageView icBack = findViewById(R.id.ic_back);
        icBack.setOnClickListener(v -> finish());

        TextView clearAll = findViewById(R.id.clear_all);
        clearAll.setOnClickListener(v -> clearAllNotifications());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));

        authService = ApiClient.getClient().create(AuthService.class);
        listViewNotifications = findViewById(R.id.list_notifications);
        notificationsList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationsList, this);
        listViewNotifications.setAdapter(adapter);

        // Load notifications initially
        loadNotifications();

        // Set up periodic notification checking
        handler = new Handler();
        notificationChecker = new Runnable() {
            @Override
            public void run() {
                loadNotifications();
                handler.postDelayed(this, 60000); // Repeat every 60 seconds
            }
        };
        handler.post(notificationChecker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && notificationChecker != null) {
            handler.removeCallbacks(notificationChecker);
        }
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", "");
        return token.isEmpty() ? "" : "Bearer " + token;
    }

    private void playNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            VibrationEffect effect = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(effect);
            }
        }
    }

    private void notifyNewNotifications() {
        String preference = getNotificationPreference();

        if ("Sound".equals(preference)) {
            playNotificationSound();
            vibrateDevice();
        } else if ("Vibrate".equals(preference)) {
            vibrateDevice();
        }
    }

    private String getNotificationPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("alert_preference", "Sound"); // Default to "Sound"
    }


    private void loadNotifications() {
        String authToken = getAuthToken();

        if (authToken.isEmpty()) {
            showStatusDialog(false, "Access token not found. Please log in.");
            return;
        }

        Call<NotificationResponse> call = authService.getUserNotifications(authToken);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<NotificationItem> newNotifications = response.body().getData();
                    if (newNotifications.size() > notificationsList.size()) {
                        notifyNewNotifications(); // Notify user of new notifications
                    }
                    notificationsList.clear();
                    notificationsList.addAll(newNotifications);
                    adapter.notifyDataSetChanged();
                } else {
                    showStatusDialog(false, "Failed to load notifications");
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                showStatusDialog(false, "Error: " + t.getMessage());
            }
        });
    }

    private void showStatusDialog(boolean isSuccess, String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_status);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.TOP;
            layoutParams.dimAmount = 0.5f;
            layoutParams.y = 0;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivStatus = dialog.findViewById(R.id.ivStatus);
        TextView tvStatus = dialog.findViewById(R.id.tvLoginStatus);
        TextView tvStatusMessage = dialog.findViewById(R.id.tvStatusMessage);

        ivStatus.setImageResource(isSuccess ? R.mipmap.tick : R.mipmap.error);
        tvStatus.setText("Notification");
        tvStatusMessage.setText(message);

        dialog.show();

        new Handler().postDelayed(dialog::dismiss, 2000);
    }

    @Override
    public void onMarkAsReadClicked(NotificationItem notification) {
        String authToken = getAuthToken();
        if (authToken.isEmpty()) return;

        Map<String, String> body = new HashMap<>();
        body.put("notificationIds", notification.getId());

        authService.markAsRead(authToken, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                    showStatusDialog(true, "Notification marked as read");
                } else {
                    showStatusDialog(false, "Failed to mark notification as read");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showStatusDialog(false, "Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDeleteClicked(NotificationItem notification) {
        String authToken = getAuthToken();
        if (authToken.isEmpty()) return;

        DeleteNotificationRequest request = new DeleteNotificationRequest(notification.getId());

        authService.deleteNotification(authToken, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notificationsList.remove(notification);
                    adapter.notifyDataSetChanged();
                    showStatusDialog(true, "Notification deleted");
                } else {
                    showStatusDialog(false, "Failed to delete notification");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showStatusDialog(false, "Error: " + t.getMessage());
            }
        });
    }

    private void clearAllNotifications() {
        String authToken = getAuthToken();
        if (authToken.isEmpty()) return;

        DeleteNotificationRequest request = new DeleteNotificationRequest(true);

        authService.deleteNotification(authToken, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notificationsList.clear();
                    adapter.notifyDataSetChanged();
                    showStatusDialog(true, "All notifications deleted");
                } else {
                    showStatusDialog(false, "Failed to delete notifications");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showStatusDialog(false, "Error: " + t.getMessage());
            }
        });
    }
}
