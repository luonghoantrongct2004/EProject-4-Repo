package com.timekeeping.timekeeping.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
public class Requestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestID;
    @NotNull
    @Column(columnDefinition = "NVARCHAR(255)")
    private String requestName;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String reason;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date requestDate;
    private String approverID;
    @Positive
    private int accountID;

    public Requestion() {
    }

    public Requestion(int requestID, String requestName, Date startDate, Date endDate, String reason, String status, Date requestDate, String approverID, int accountID) {
        this.requestID = requestID;
        this.requestName = requestName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.requestDate = requestDate;
        this.approverID = approverID;
        this.accountID = accountID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getApproverID() {
        return approverID;
    }

    public void setApproverID(String approverID) {
        this.approverID = approverID;
    }

    @Positive
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(@Positive int accountID) {
        this.accountID = accountID;
    }
}
