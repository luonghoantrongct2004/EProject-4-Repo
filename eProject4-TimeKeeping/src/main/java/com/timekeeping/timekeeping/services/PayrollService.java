package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Payroll;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.models.SalaryTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollService {

    @PersistenceContext
    private EntityManager entityManager;

//    @Transactional
//    public Payroll createPayroll(Payroll payroll) {
//        payroll.calculateNetSalary();
//        entityManager.persist(payroll);
//        return payroll;
//    }
    //@Scheduled(cron = "0 0 0 1/30 * ?") @Scheduled(cron = "0 0 0 1/30 * ?")
    @Transactional
   // @Scheduled(cron = "*/15 * * * * *") // Run every 30 seconds
    @Scheduled(cron = "0 0 0 1/30 * ?")


    public void scheduledGeneratePayroll() {
        List<Account> accounts = getAllAccounts();
        LocalDate today = LocalDate.now();  // Get the current date

        for (Account account : accounts) {
            // Find attendance records by account and month
            List<AttendanceRecord> attendances = findAttendancesByAccountAndMonth(account, today);

            if (!attendances.isEmpty()) {
                generatePayroll(account, attendances);
            } else {
                System.out.println("No attendance found for account: " + account.getFullName() + " in the current month.");
            }
        }
    }

    @Transactional
    public void generatePayroll(Account account, List<AttendanceRecord> attendances) {
        if (attendances.isEmpty()) {
            throw new RuntimeException("No attendance records found for the account on the specified date.");
        }

        // Total work hours for the employee based on attendance records
        double totalWorkHours = attendances.stream()
                .mapToDouble(AttendanceRecord::calculateWorkHours)
                .sum();

        // Expected monthly work hours (173.33 hours based on 8-hour workdays, 5 days a week)
        double expectedMonthlyHours = 173.33;  // Adjusted for 8 hours/day, 5 days/week, no work on weekends

        // Calculate hourly rate from monthly base salary
        double hourlyRate = account.getSalaryTemplate().getBaseSalary() / expectedMonthlyHours;

        // Calculate gross salary based on actual work hours
        double grossSalary = hourlyRate * totalWorkHours;

        // Create new Payroll object
        Payroll payroll = new Payroll();
        payroll.setAccount(account);
        payroll.setSalaryTemplate(account.getSalaryTemplate());
        payroll.setGrossSalary(grossSalary);

        // Calculate net salary, passing necessary parameters
        payroll.calculateNetSalary(payroll, attendances, account.getSalaryTemplate());

        System.out.println("Generated Payroll for account: " + account.getFullName() + " with grossSalary: " + grossSalary);

        // Persist payroll record
        entityManager.persist(payroll);
    }

    public void calculateNetSalary(Payroll payroll, List<AttendanceRecord> attendanceRecords, SalaryTemplate salaryTemplate) {
        double baseSalary = salaryTemplate.getBaseSalary(); // Lương cơ bản theo tháng
        double standardWorkingDays = 22; // Giả sử 22 ngày làm việc trong tháng
        double standardWorkingHours = 8; // Số giờ làm việc tiêu chuẩn mỗi ngày

        // Tính lương theo giờ
        double hourlyRate = baseSalary / (standardWorkingDays * standardWorkingHours);

        // Tổng số giờ làm việc của nhân viên, tính từ clockInTime và clockOutTime
        double totalWorkingHours = attendanceRecords.stream()
                .mapToDouble(this::calculateWorkingHours) // Tính số giờ làm việc từ mỗi bản ghi chấm công
                .sum();

        // Tính lương dựa trên số giờ làm việc thực tế
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

        // Lưu lương thực nhận vào payroll
        payroll.setNetSalary(netSalary);
    }

    private double calculateWorkingHours(AttendanceRecord attendanceRecord) {
        LocalDateTime clockInTime = attendanceRecord.getClockInTime();
        LocalDateTime clockOutTime = attendanceRecord.getClockOutTime();

        if (clockInTime == null || clockOutTime == null) {
            // Nếu không có thời gian chấm công hợp lệ, giả sử không có giờ làm việc
            return 0.0;
        }

        // Tính số giờ giữa thời gian vào làm và thời gian ra về
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


    public List<Payroll> findPayrollsByCreationDate(LocalDate date) {
        String jpql = "SELECT p FROM Payroll p WHERE DATE(p.createdAt) = :date";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("date", date)
                .getResultList();
    }
    public List<Payroll> findPayrollsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT p FROM Payroll p WHERE p.createdAt BETWEEN :startDate AND :endDate";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Transactional
    public List<Payroll> findPayrollsByAccount(Account account) {
        String jpql = "SELECT p FROM Payroll p WHERE p.account = :account";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("account", account)
                .getResultList();
    }

    public List<Payroll> findByName(String name) {
        return entityManager.createQuery("FROM Payroll WHERE account.fullName LIKE :name", Payroll.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }
    public List<AttendanceRecord> findAttendancesByAccountAndMonth(Account account, LocalDate date) {
        int year = date.getYear();   // Extract year from the provided date
        int month = date.getMonthValue();  // Extract month from the provided date

        // Query to find attendance records by year and month
        List<AttendanceRecord> attendances = entityManager.createQuery(
                        "SELECT a FROM AttendanceRecord a WHERE a.account = :account AND YEAR(a.date) = :year AND MONTH(a.date) = :month", AttendanceRecord.class)
                .setParameter("account", account)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();

        System.out.println("Found " + attendances.size() + " attendance records for account: " + account.getFullName() + " in month: " + month + "/" + year);
        return attendances;
    }

    public List<AttendanceRecord> findAttendancesByAccountAndDate(Account account, LocalDate date) {
        List<AttendanceRecord> attendances = entityManager.createQuery(
                        "SELECT a FROM AttendanceRecord a WHERE a.account = :account AND a.date = :date", AttendanceRecord.class)
                .setParameter("account", account)
                .setParameter("date", date)
                .getResultList();

        System.out.println("Found " + attendances.size() + " attendance records for account: " + account.getFullName());
        return attendances;
    }
    @Transactional
    public Payroll updatePayroll(Payroll payroll) {
        entityManager.merge(payroll);
        return payroll;
    }

    public Payroll findPayrollById(int payrollID) {
        return entityManager.find(Payroll.class, payrollID);
    }

    @Transactional
    public void deletePayroll(int payrollID) {
        Payroll payroll = findPayrollById(payrollID);
        if (payroll != null) {
            entityManager.remove(payroll);
        }
    }

    public List<Payroll> findAllPayrolls() {
        return entityManager.createQuery("SELECT p FROM Payroll p", Payroll.class).getResultList();
    }
    public List<Account> getAllAccounts() {
        return entityManager.createQuery("SELECT a FROM Account a", Account.class).getResultList();
    }


//    @Transactional
//    public void updateStaticPayrollData() {
//        List<Payroll> payrolls = findAllPayrolls(); // Lấy tất cả payroll từ database
//
//        for (Payroll payroll : payrolls) {
//            // Gọi hàm tính toán lương
//            calculateNetSalary(payroll);
//
//            // Cập nhật lại payroll với lương mới
//            entityManager.merge(payroll);
//        }
//    }
}
