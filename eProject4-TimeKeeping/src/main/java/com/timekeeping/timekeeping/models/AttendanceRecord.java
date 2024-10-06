package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordID;
    //    private int accountID;
    private LocalDate date;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String status;
    private String notes;
    private String formattedWorkingHours;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;  // Reference to the Shift entity

    // Constructors, getters, and setters

    public AttendanceRecord() {
    }

    public AttendanceRecord(int recordID, LocalDate date, LocalDateTime clockInTime, LocalDateTime clockOutTime, String status, String notes, String formattedWorkingHours, Shift shift) {
        this.recordID = recordID;
//        this.accountID = accountID;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.status = status;
        this.notes = notes;
        this.formattedWorkingHours = formattedWorkingHours;
        this.shift = shift;
    }

    // Getters and Setters

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

//    public int getAccountID() {
//        return accountID;
//    }
//
//    public void setAccountID(int accountID) {
//        this.accountID = accountID;
//    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getFormattedWorkingHours() {
        return formattedWorkingHours;
    }

    public void setFormattedWorkingHours(String formattedWorkingHours) {
        this.formattedWorkingHours = formattedWorkingHours;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
    // Calculate formatted work hours as string
    // Calculate formatted work hours as string in "0h1m" format
    public String calculateFormattedWorkHours() {
        if (clockInTime != null && clockOutTime != null) {
            Duration duration = Duration.between(clockInTime, clockOutTime);
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;

            // Format hours and minutes as "XhYm"
            return String.format("%dh%02dm", hours, minutes);
        }
        return "0h0m"; // Return "0h0m" if either clockInTime or clockOutTime is null
    }


    // Calculate actual working hours as a decimal value
    public double calculateWorkHours() {
        if (clockInTime != null && clockOutTime != null) {
            Duration duration = Duration.between(clockInTime, clockOutTime);
            return duration.toHours() + (duration.toMinutesPart() / 60.0);
        }
        return 0;
    }
}
