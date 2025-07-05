package com.example.finance_tracker.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification_preferences")
public class NotificationPreference {

    @Id
    private String id;
    private String userId;
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean inAppEnabled;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public void setInAppEnabled(boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }
}