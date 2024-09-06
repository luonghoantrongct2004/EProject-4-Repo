package com.timekeeping.timekeeping.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int regionID;
    private String regionName;
    private double minimumWage;

    public Region() {
    }

    public Region(String regionName, double minimumWage) {
        this.regionName = regionName;
        this.minimumWage = minimumWage;
    }

    // Getters and Setters

    public int getRegionID() {
        return regionID;
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public double getMinimumWage() {
        return minimumWage;
    }

    public void setMinimumWage(double minimumWage) {
        this.minimumWage = minimumWage;
    }
}
