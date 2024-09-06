package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;

@Entity
public class Deduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deductionID;
    private String deductionType;
    private String description;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    // Constructors
    public Deduction() {
    }

    public Deduction(int deductionID, String deductionType, String description, double amount) {
        this.deductionID = deductionID;
        this.deductionType = deductionType;
        this.description = description;
        this.amount = amount;
    }

    // Getters and Setters
    public int getDeductionID() {
        return deductionID;
    }

    public void setDeductionID(int deductionID) {
        this.deductionID = deductionID;
    }

    public String getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(String deductionType) {
        this.deductionType = deductionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Payroll getPayroll() {
        return payroll;
    }

    public void setPayroll(Payroll payroll) {
        this.payroll = payroll;
    }

    // Method to apply the deduction to the gross salary
    public double applyDeduction(double grossSalary) {
        return grossSalary - this.amount;
    }

    // Optionally, you can add other methods like toString, equals, hashCode, etc.
}
