package com.example.potholepatrol.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.potholepatrol.model.NotificationItem;
import com.example.potholepatrol.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
    private OnNotificationActionListener actionListener;

    public interface OnNotificationActionListener {
        void onDeleteClicked(NotificationItem notification);
        void onMarkAsReadClicked(NotificationItem notification);
    }

    public NotificationAdapter(Context context, List<NotificationItem> notifications, OnNotificationActionListener listener) {
        super(context, 0, notifications);
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
        }

        NotificationItem notification = getItem(position);
        if (notification == null) return convertView;

        TextView title = convertView.findViewById(R.id.tvTitle);
        TextView message = convertView.findViewById(R.id.tvMessage);
        TextView time = convertView.findViewById(R.id.tvTime);
        ImageView deleteButton = convertView.findViewById(R.id.ivDelete);
        ImageView markAsReadButton = convertView.findViewById(R.id.ivMarkAsRead);

        title.setText(notification.getTitle());
        message.setText(notification.getMessage());
        time.setText(formatTime(notification.getCreatedAt()));

        // Set button states and listeners
        deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClicked(notification);
            }
        });

        markAsReadButton.setOnClickListener(v -> {
            if (!notification.isRead() && actionListener != null) {
                actionListener.onMarkAsReadClicked(notification);
            }
        });

        // Update UI based on read status
        if (notification.isRead()) {
            markAsReadButton.setImageResource(R.mipmap.ic_mark_as_read_done);
            convertView.setAlpha(0.7f);
        } else {
            markAsReadButton.setImageResource(R.mipmap.ic_mark_as_read);
            convertView.setAlpha(1.0f);
        }

        return convertView;
    }

    private String formatTime(String rawTime) {
        try {
            Date date = inputFormat.parse(rawTime);
            return date != null ? outputFormat.format(date) : rawTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return rawTime;
        }
    }
}