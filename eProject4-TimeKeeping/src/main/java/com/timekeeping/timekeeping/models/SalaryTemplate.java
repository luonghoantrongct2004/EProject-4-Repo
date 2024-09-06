package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;

@Entity
public class SalaryTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryID;
    private String gradeName;
    private double baseSalary;

    @ManyToOne
    @JoinColumn(name = "regionID")
    private Region region;

    public SalaryTemplate() {
    }

    public SalaryTemplate(String gradeName, double baseSalary, Region region) {
        this.gradeName = gradeName;
        this.baseSalary = baseSalary;
        this.region = region;
    }

    // Getters and Setters

    public int getSalaryID() {
        return salaryID;
    }

    public void setSalaryID(int salaryID) {
        this.salaryID = salaryID;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public double calculateFinalBaseSalary() {
        return Math.max(this.baseSalary, this.region.getMinimumWage());
    }
}
