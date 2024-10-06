package com.timekeeping.timekeeping.enums;

public enum ActivityStatus {
    ACTIVE("Đang Diễn Ra"),
    INACTIVE("Đã Kết Thúc"),
    PENDING("Chưa Bắt Đầu");

    private String displayName;

    ActivityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}