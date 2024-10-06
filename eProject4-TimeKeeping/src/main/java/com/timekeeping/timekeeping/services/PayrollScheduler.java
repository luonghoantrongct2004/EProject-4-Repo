package com.timekeeping.timekeeping.services;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
public class PayrollScheduler {
    @Autowired
    private PayrollService payrollService;

    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào lúc 00:00 mỗi ngày
    public void calculateDailyPayrolls() {
        // Lấy tất cả các nhân viên và gọi phương thức generatePayroll cho mỗi nhân viên
        List<Account> accounts = payrollService.getAllAccounts();
        for (Account account : accounts) {
            List<AttendanceRecord> attendances = payrollService.findAttendancesByAccountAndDate(account, LocalDate.now());
            payrollService.generatePayroll(account, attendances);
        }
    }

}
