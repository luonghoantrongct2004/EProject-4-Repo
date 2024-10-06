package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.AttendanceRecordRepository;
import com.timekeeping.timekeeping.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/attendance")
public class AttendanceRecordController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    /**
     * API cho chấm công vào (Clock In)
     */

    /**
     * API để tự động chấm công vào hoặc chấm công ra dựa trên trạng thái hiện tại.
     * Nếu người dùng đã chấm công vào, nó sẽ thực hiện chấm công ra.
     * Ngược lại, nếu chưa chấm công vào, nó sẽ thực hiện chấm công vào.
     */
    @PostMapping("/autoClock")
    public ResponseEntity<?> autoClock(@RequestParam int accountID) {
        try {
            // Kiểm tra tính hợp lệ của account ID
            if (accountID <= 0) {
                return ResponseEntity.badRequest().body("ID tài khoản không hợp lệ. Phải là một số nguyên dương.");
            }

            // Lấy thông tin tài khoản
            Account account = accountRepository.findById(accountID)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với ID: " + accountID));

            LocalDateTime currentTime = LocalDateTime.now();

            // Kiểm tra xem có bản ghi chấm công vào nào đang hoạt động (không có thời gian chấm công ra)
            Optional<AttendanceRecord> existingRecordOpt = attendanceRecordRepository
                    .findFirstByAccountAndClockOutTimeIsNullOrderByClockInTimeDesc(account);

            if (existingRecordOpt.isPresent()) {
                // Nếu có bản ghi chấm công vào đang hoạt động, thực hiện chấm công ra
                return ResponseEntity.ok(attendanceService.autoClockOut(accountID, currentTime));
            } else {
                // Nếu không có bản ghi chấm công vào nào, thực hiện chấm công vào
                return ResponseEntity.ok(attendanceService.autoClockIn(accountID, currentTime));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi trong quá trình chấm công: " + e.getMessage());
        }
    }


    /**
     * API để lấy tất cả bản ghi chấm công của một tài khoản cụ thể
     */
    @GetMapping("/show")
    public String showAttendance(@RequestParam(required = false) Integer accountID, Model model) {
        List<AttendanceRecord> attendance;

        if (accountID != null && accountID != 0) {
            attendance = attendanceService.getAttendanceRecordsByAccountID(accountID);
            if (attendance == null || attendance.isEmpty()) {
                model.addAttribute("message", "No attendance records available for Account ID: " + accountID);
            } else {
                model.addAttribute("attendance", attendance);
            }
        } else {
            attendance = attendanceService.getAllAttendanceRecords();
            if (attendance == null || attendance.isEmpty()) {
                model.addAttribute("message", "No attendance records available.");
            } else {
                model.addAttribute("attendance", attendance);
            }
        }

        // Persist the accountID in the search input
        model.addAttribute("accountID", accountID);

        return "home/attendace-show"; // Template path: src/main/resources/templates/home/attendance-show.html
    }

}
