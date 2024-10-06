package com.timekeeping.timekeeping.enums;

public enum ParticipationStatus {
    JOINED("Joined"),
    DENIED("Denied"),
    REASON_APCEPTED("Reason has been apcepted"),
    REASON_DENIED("Reason has been denied");

    private String displayName;

    ParticipationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
