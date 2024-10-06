package com.timekeeping.timekeeping.enums;

public enum RequestionStatus {
    PENDING("Đang chờ phê duyệt"),
    APPROVED("Đã Chấp Thuận"),
    REJECTED("Đã bị từ chối");

    private String description;

    RequestionStatus(String description) {this.description = description;}

    public String getDescription() {return description;}
}
