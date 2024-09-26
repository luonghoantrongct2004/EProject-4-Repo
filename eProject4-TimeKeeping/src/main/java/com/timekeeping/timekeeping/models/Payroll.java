package com.timekeeping.timekeeping.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<Deduction> deductions = new ArrayList<>();

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bonus> bonuses = new ArrayList<>();

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


    public void calculateNetSalary(Payroll payroll, List<AttendanceRecord> attendanceRecords, SalaryTemplate salaryTemplate) {
        double baseSalary = salaryTemplate.getBaseSalary(); // Lương cơ bản theo tháng
        double standardWorkingDays = 22; // Giả sử 22 ngày làm việc trong tháng
        double standardWorkingHours = 8; // Số giờ làm việc tiêu chuẩn mỗi ngày

        // Tính tổng số giờ làm việc tiêu chuẩn trong tháng
        double totalStandardHoursInMonth = standardWorkingDays * standardWorkingHours;

        // Tổng số giờ làm việc thực tế từ các bản ghi chấm công
        double totalWorkingHours = attendanceRecords.stream()
                .mapToDouble(this::calculateWorkingHours)
                .sum();

        // Tính lương theo giờ
        double hourlyRate = baseSalary / totalStandardHoursInMonth;

        // Tính lương gộp dựa trên số giờ làm việc thực tế
        double grossSalary = hourlyRate * totalWorkingHours;

        // Tính bảo hiểm xã hội, bảo hiểm y tế, bảo hiểm thất nghiệp
        double bhxh = grossSalary * 0.08;
        double bhyt = grossSalary * 0.015;
        double bhtn = grossSalary * 0.01;
        double totalInsurance = bhxh + bhyt + bhtn;

        // Thu nhập chịu thuế
        double taxableIncome = grossSalary - totalInsurance;

        // Tính thuế thu nhập cá nhân (PIT)
        double pit = calculatePIT(taxableIncome);

        // Tính lương thực nhận (Net Salary)
        double netSalary = grossSalary - totalInsurance - pit;

        grossSalary = grossSalary + baseSalary;
        // Lưu lương thực nhận vào payroll
        payroll.setNetSalary(netSalary);
        payroll.setGrossSalary(grossSalary);
    }



    private double calculateWorkingHours(AttendanceRecord attendanceRecord) {
        LocalDateTime clockInTime = attendanceRecord.getClockInTime();
        LocalDateTime clockOutTime = attendanceRecord.getClockOutTime();

        if (clockInTime == null || clockOutTime == null) {
            return 0.0;  // Không có giờ làm việc nếu thời gian vào/ra không hợp lệ
        }

        // Tính số giờ làm việc
        Duration duration = Duration.between(clockInTime, clockOutTime);
        double workingHours = duration.toHours() + (duration.toMinutesPart() / 60.0);
        return workingHours;
    }


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
