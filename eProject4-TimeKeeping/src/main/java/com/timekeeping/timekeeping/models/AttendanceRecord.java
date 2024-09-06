package com.timekeeping.timekeeping.models;


import jakarta.persistence.*;

import java.time.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordID;
    private int accountID;
    private LocalDate date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String status;
    private String notes;
    private double workingHours;
    private int shiftID;
    // e.g., Applied, Interviewed, Hired, Rejected
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    // Getters and Setters

    public AttendanceRecord() {
    }

    public AttendanceRecord(int recordID, int accountID, LocalDate date, LocalDateTime clockInTime, LocalDateTime clockOutTime, String status, String notes, double workingHours, int shiftID) {
        this.recordID = recordID;
        this.accountID = accountID;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.status = status;
        this.notes = notes;
        this.workingHours = workingHours;
        this.shiftID = shiftID;
    }
    
    

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate Date) {
        this.date = Date;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(double workingHours) {
        this.workingHours = workingHours;
    }

    public int getShiftID() {
        return shiftID;
    }

    public void setShiftID(int shiftID) {
        this.shiftID = shiftID;
    }
    public double calculateWorkHours() {
        return Duration.between(clockInTime, clockOutTime).toHours();
    }

    

   

}