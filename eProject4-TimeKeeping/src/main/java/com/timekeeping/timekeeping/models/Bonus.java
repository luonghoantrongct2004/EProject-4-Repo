package com.timekeeping.timekeeping.models;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bonusID;
    @Column(name = "bonus_type", columnDefinition = "nvarchar(255)")
    private String bonusType;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    // Các phương thức getter và setter
    public int getBonusID() {
        return bonusID;
    }

    public void setBonusID(int bonusID) {
        this.bonusID = bonusID;
    }

    public String getBonusType() {
        return bonusType;
    }

    public void setBonusType(String bonusType) {
        this.bonusType = bonusType;
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
}

