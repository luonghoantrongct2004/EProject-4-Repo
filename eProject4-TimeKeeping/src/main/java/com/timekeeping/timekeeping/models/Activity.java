package com.timekeeping.timekeeping.models;

import com.timekeeping.timekeeping.enums.ActivityType;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int activityId;

    private String activityName;
    @Column(columnDefinition = "NTEXT")
    private String description;
    private LocalDateTime startTime;
    private double budget;
    private String location;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    public Activity() {
    }

    public Activity(int activityId, String activityName, String description, LocalDateTime startTime, double budget, String location, ActivityType type) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.description = description;
        this.startTime = startTime;
        this.budget = budget;
        this.location = location;
        this.type = type;
    }

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }
}

