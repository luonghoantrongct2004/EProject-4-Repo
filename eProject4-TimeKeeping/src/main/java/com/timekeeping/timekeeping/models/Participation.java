package com.timekeeping.timekeeping.models;

import com.timekeeping.timekeeping.enums.ParticipationStatus;
import jakarta.persistence.*;

@Entity
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int participateId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "id")
    private ActivityNotification activityNotification;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    public Participation() {
    }

    public Participation(int participateId, Account account, ActivityNotification activityNotification, String reason, ParticipationStatus status) {
        this.participateId = participateId;
        this.account = account;
        this.activityNotification = activityNotification;
        this.reason = reason;
        this.status = status;
    }

    public int getParticipateId() {
        return participateId;
    }

    public void setParticipateId(int participateId) {
        this.participateId = participateId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ActivityNotification getActivityNotification() {
        return activityNotification;
    }

    public void setActivityNotification(ActivityNotification activityNotification) {
        this.activityNotification = activityNotification;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }
}
