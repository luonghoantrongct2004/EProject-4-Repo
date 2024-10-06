package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int activityId;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String activityName;

    @Column(columnDefinition = "NTEXT")
    private String description;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String purpose;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "mandatory")
    private boolean mandatory;

    private double budget;

    // Constructors
    public Activity() {}

    public Activity(int activityId, String activityName, String description, String purpose, LocalDate date, boolean mandatory, double budget) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.description = description;
        this.purpose = purpose;
        this.date = date;
        this.mandatory = mandatory;
        this.budget = budget;
    }

    // Getters and Setters
    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
