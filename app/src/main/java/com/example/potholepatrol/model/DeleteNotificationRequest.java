package com.example.potholepatrol.model;

import com.google.gson.annotations.SerializedName;

public class DeleteNotificationRequest {
    @SerializedName("notificationIds")
    private String notificationIds;

    @SerializedName("deleteAll")
    private Boolean deleteAll;

    // Constructor for single notification delete
    public DeleteNotificationRequest(String notificationIds) {
        this.notificationIds = notificationIds;
        this.deleteAll = null;
    }

    // Constructor for delete all
    public DeleteNotificationRequest(Boolean deleteAll) {
        this.deleteAll = deleteAll;
        this.notificationIds = null;
    }
}