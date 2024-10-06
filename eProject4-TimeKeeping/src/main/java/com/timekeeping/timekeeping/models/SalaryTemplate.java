package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class SalaryTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salaryID;
    private String gradeName;
    private double baseSalary;
    // Ngày hiệu lực
    private LocalDate effectiveDate;

    // Ngày hết hạn
    private LocalDate expiryDate;
    @ManyToOne
    @JoinColumn(name = "regionID")
    private Region region;

    public SalaryTemplate() {
    }

    public SalaryTemplate(int salaryID, String gradeName, double baseSalary, LocalDate effectiveDate, LocalDate expiryDate, Region region) {
        this.salaryID = salaryID;
        this.gradeName = gradeName;
        this.baseSalary = baseSalary;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.region = region;
    }

    // Getters and Setters
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
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
