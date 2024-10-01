package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ActivityNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime notificationTime;
    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;  // Người nhận thông báo

    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activity;  // Sự kiện liên quan đến thông báo

    public ActivityNotification() {
    }

    public ActivityNotification(Long id, String content, LocalDateTime notificationTime, boolean isRead, Account account, Activity activity) {
        this.id = id;
        this.content = content;
        this.notificationTime = notificationTime;
        this.isRead = isRead;
        this.account = account;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
