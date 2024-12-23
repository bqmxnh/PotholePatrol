package com.example.potholepatrol.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.potholepatrol.Dashboard.ActivityDashboardNotification;
import com.example.potholepatrol.R;
import com.example.potholepatrol.api.ApiClient;
import com.example.potholepatrol.api.AuthService;
import com.example.potholepatrol.model.NotificationItem;
import com.example.potholepatrol.model.NotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "pothole_patrol_channel";
    private static final int NOTIFICATION_ID = 1;
    private AuthService authService;
    private Handler handler;
    private Runnable notificationChecker;

    @Override
    public void onCreate() {
        super.onCreate();
        authService = ApiClient.getClient().create(AuthService.class);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildForegroundNotification());
        startPeriodicNotificationCheck();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Đảm bảo service tự khởi động lại nếu bị dừng
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(notificationChecker);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Không cần bind trong service chạy nền
    }


    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pothole Patrol")
                .setContentText("Notifications will be displayed here")
                .setSmallIcon(R.mipmap.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pothole Patrol Notifications",
                    NotificationManager.IMPORTANCE_HIGH // Đặt độ ưu tiên cao
            );
            channel.setDescription("Channel for Pothole Patrol notifications");

            // Bật âm thanh
            channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), // Âm thanh mặc định
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );

            // Bật rung
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500}); // Mẫu rung: Dừng, rung 500ms, dừng 200ms, rung 500ms

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    private void startPeriodicNotificationCheck() {
        handler = new Handler();
        notificationChecker = new Runnable() {
            @Override
            public void run() {
                checkForNewNotifications();
                handler.postDelayed(this, 1000); // Kiểm tra thông báo mỗi phút
            }
        };
        handler.post(notificationChecker);
    }

    private void checkForNewNotifications() {
        String authToken = getAuthToken();

        if (authToken.isEmpty()) {
            return; // Không kiểm tra nếu không có token
        }

        Call<NotificationResponse> call = authService.getUserNotifications(authToken);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<NotificationItem> notifications = response.body().getData();
                    int unreadCount = 0;
                    NotificationItem firstUnreadNotification = null;

                    for (NotificationItem item : notifications) {
                        if (!item.isRead() && !isNotificationShown(item.getId())) {
                            if (firstUnreadNotification == null) {
                                firstUnreadNotification = item;
                            }
                            unreadCount++;
                        }
                    }

                    if (firstUnreadNotification != null) {
                        showNotification(firstUnreadNotification, unreadCount);
                        markNotificationAsShown(firstUnreadNotification.getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                // Log lỗi nếu cần
            }
        });
    }


    private boolean isNotificationShown(String notificationId) {
        SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        return preferences.getBoolean(notificationId, false);
    }

    private void markNotificationAsShown(String notificationId) {
        SharedPreferences preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(notificationId, true);
        editor.apply();
    }

    private void showNotification(NotificationItem notification, int unreadCount) {
        Intent intent = new Intent(this, ActivityDashboardNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String contentText = notification.getMessage();
        if (unreadCount > 1) {
            contentText += " + " + (unreadCount - 1) + " more unread notifications";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Độ ưu tiên cao để hiển thị trên cùng
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL); // Bao gồm rung, âm thanh và đèn LED (nếu có)

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }



    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", "");
        return token.isEmpty() ? "" : "Bearer " + token;
    }
}
