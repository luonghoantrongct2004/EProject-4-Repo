package com.timekeeping.timekeeping.enums;

public enum ApprovalStatus {
    PENDING("Đang Chờ Duyệt"),
    APPROVED("Đã Chấp Nhận"),
    REJECTED("Bị Từ Chối");

    private String displayName;

    ApprovalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}