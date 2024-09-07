package com.timekeeping.timekeeping.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.timekeeping.timekeeping.services.PayrollService;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payrollID;

    @ManyToOne
    @JoinColumn(name = "accountID", referencedColumnName = "accountID")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "salaryID", referencedColumnName = "salaryID")
    private SalaryTemplate salaryTemplate;

    private LocalDate payDate;
    private double grossSalary;
    private double netSalary;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Deduction> deductions;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bonus> bonuses;

    @CreationTimestamp
    private LocalDateTime createdAt;
    // Các phương thức getter và setter
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public int getPayrollID() {
        return payrollID;
    }

    public void setPayrollID(int payrollID) {
        this.payrollID = payrollID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public LocalDate  getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDate  payDate) {
        this.payDate = payDate;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<Deduction> deductions) {
        this.deductions = deductions;
    }

    public List<Bonus> getBonuses() {
        return bonuses;
    }

    public void setBonuses(List<Bonus> bonuses) {
        this.bonuses = bonuses;
    }

    public void calculateNetSalary() {
        if (salaryTemplate.getEffectiveDate().isAfter(payDate)) {
            throw new IllegalStateException("Mẫu lương chưa có hiệu lực vào ngày thanh toán.");
        }

        if (salaryTemplate.getExpiryDate() != null && salaryTemplate.getExpiryDate().isBefore(payDate)) {
            throw new IllegalStateException("Mẫu lương đã hết hạn vào ngày thanh toán.");
        }
        double bhxh = grossSalary * 0.08;
        double bhyt = grossSalary * 0.015;
        double bhtn = grossSalary * 0.01;
        double totalInsurance = bhxh + bhyt + bhtn;

        double taxableIncome = grossSalary - totalInsurance;
        double pit = calculatePIT(taxableIncome);

        this.netSalary = grossSalary - totalInsurance - pit;
    }
//    TNCN được tính dựa trên thu nhập chịu thuế theo biểu thuế lũy tiến từng phần:
//
//    Thu nhập chịu thuế hàng tháng (VND)	Thuế suất TNCN
//    Đến 5 triệu	5%
//            5 - 10 triệu	10%
//            10 - 18 triệu	15%
//            18 - 32 triệu	20%
//            32 - 52 triệu	25%
//            52 - 80 triệu	30%
//    Trên 80 triệu	35%
//Bảo hiểm xã hội (BHXH):
//    Phần người lao động đóng = 8% lương Gross.
//    Bảo hiểm y tế (BHYT):
//    Phần người lao động đóng = 1,5% lương Gross.
//    Bảo hiểm thất nghiệp (BHTN):
//    Phần người lao động đóng = 1% lương Gross.
    // Phương thức tính thuế TNCN theo biểu thuế lũy tiến của Việt Nam
    private double calculatePIT(double taxableIncome) {
        double pit = 0;
        if (taxableIncome <= 5000000) {
            pit = taxableIncome * 0.05;
        } else if (taxableIncome <= 10000000) {
            pit = 5000000 * 0.05 + (taxableIncome - 5000000) * 0.1;
        } else if (taxableIncome <= 18000000) {
            pit = 5000000 * 0.05 + 5000000 * 0.1 + (taxableIncome - 10000000) * 0.15;
        } else if (taxableIncome <= 32000000) {
            pit = 5000000 * 0.05 + 5000000 * 0.1 + 8000000 * 0.15 + (taxableIncome - 18000000) * 0.2;
        } else if (taxableIncome <= 52000000) {
            pit = 5000000 * 0.05 + 5000000 * 0.1 + 8000000 * 0.15 + 14000000 * 0.2 + (taxableIncome - 32000000) * 0.25;
        } else if (taxableIncome <= 80000000) {
            pit = 5000000 * 0.05 + 5000000 * 0.1 + 8000000 * 0.15 + 14000000 * 0.2 + 20000000 * 0.25 + (taxableIncome - 52000000) * 0.3;
        } else {
            pit = 5000000 * 0.05 + 5000000 * 0.1 + 8000000 * 0.15 + 14000000 * 0.2 + 20000000 * 0.25 + 28000000 * 0.3 + (taxableIncome - 80000000) * 0.35;
        }
        return pit;
    }

}
