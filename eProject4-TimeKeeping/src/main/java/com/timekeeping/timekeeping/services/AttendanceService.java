package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.models.Shift;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.AttendanceRecordRepository;
import com.timekeeping.timekeeping.repositories.ShiftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ShiftRepository shiftRepository;

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final long LATE_THRESHOLD_MINUTES = 15;
    @Transactional
    public ResponseEntity<?> autoClock(int accountID) {
        try {
            // Kiểm tra tính hợp lệ của accountID
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
                return ResponseEntity.ok(autoClockOut(accountID, currentTime));
            } else {
                // Nếu không có bản ghi chấm công vào nào, thực hiện chấm công vào
                return ResponseEntity.ok(autoClockIn(accountID, currentTime));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi trong quá trình chấm công: " + e.getMessage());
        }
    }

    /**
     * Chấm công vào (Clock In)
     */
    @Transactional
    public AttendanceRecord autoClockIn(int accountID, LocalDateTime clockInTime) {
        Account account = accountRepository.findById(accountID)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với ID: " + accountID));

        Shift currentShift = determineCurrentShift();
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setAccount(account);
        attendanceRecord.setClockInTime(clockInTime);
        attendanceRecord.setStatus("IN");
        attendanceRecord.setDate(clockInTime.toLocalDate());
        attendanceRecord.setShift(currentShift);
        LocalTime shiftStartTime = currentShift.getStartTime();
        LocalTime actualClockInTime = clockInTime.toLocalTime();
        long lateMinutes = 0;
        if (actualClockInTime.isAfter(shiftStartTime)) {
            Duration duration = Duration.between(shiftStartTime, actualClockInTime);
            lateMinutes = duration.toMinutes();
        }
        if (lateMinutes >= 15) {
            attendanceRecord.setNotes(String.format("Đến trễ %d phút", lateMinutes));
        } else {
            attendanceRecord.setNotes("Đúng giờ");
        }

        return attendanceRecordRepository.save(attendanceRecord);
    }

    /**
     * Chấm công ra (Clock Out)
     */
    @Transactional
    public AttendanceRecord autoClockOut(int accountID, LocalDateTime clockOutTime) {
        Account account = accountRepository.findById(accountID)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với ID: " + accountID));

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findFirstByAccountAndClockOutTimeIsNullOrderByClockInTimeDesc(account)
                .orElseThrow(() -> new IllegalArgumentException("Không có bản ghi chấm công vào hợp lệ"));

        Shift clockInShift = attendanceRecord.getShift();
        Shift currentShift = determineCurrentShift();
        if (currentShift.getShiftId() != clockInShift.getShiftId()) {
            logger.warn("Shift mismatch: Clock-out shift (ID: {}) does not match clock-in shift (ID: {}).",
                    currentShift.getShiftId(), clockInShift.getShiftId());
        }

        attendanceRecord.setClockOutTime(clockOutTime);
        attendanceRecord.setStatus("OUT");

        // Tính số giờ làm việc
        Duration workDuration = Duration.between(attendanceRecord.getClockInTime(), clockOutTime);

        // Lấy thời gian kết thúc của ca làm
        LocalTime shiftEndTime = clockInShift.getEndTime();
        LocalTime actualClockOutTime = clockOutTime.toLocalTime();
        if (actualClockOutTime.isBefore(shiftEndTime)) {
            // Tính thời gian rời sớm
            Duration earlyDuration = Duration.between(actualClockOutTime, shiftEndTime);
            attendanceRecord.setNotes(String.format("Rời sớm %d phút", earlyDuration.toMinutes()));
        } else {
            // Hoàn thành ca làm
            attendanceRecord.setNotes("Hoàn thành ca làm");
        }

        long hours = workDuration.toHours();
        long minutes = workDuration.toMinutes() % 60;
        String formattedWorkingHours = String.format("%dh%02dm", hours, minutes);
        attendanceRecord.setFormattedWorkingHours(formattedWorkingHours);

        return attendanceRecordRepository.save(attendanceRecord);
    }

    /**
     * Lấy tất cả bản ghi chấm công của một tài khoản cụ thể
     */
    public List<AttendanceRecord> getAttendanceRecordsByAccountID(int accountID) {
        Account account = accountRepository.findById(accountID)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với ID: " + accountID));

        return attendanceRecordRepository.findByAccount(account);
    }
    public List<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceRecordRepository.findAll();
    }
    public double calculateTotalWorkingHours(int accountId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getAttendanceRecordsByAccountIdAndDateRange(accountId, startDate, endDate);
        return records.stream()
                .filter(record -> record.getClockInTime() != null && record.getClockOutTime() != null)
                .mapToDouble(record -> calculateWorkingHours(record.getClockInTime(), record.getClockOutTime()))
                .sum();
    }
    public static double calculateWorkingHours(LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        if (clockInTime == null || clockOutTime == null || clockOutTime.isBefore(clockInTime)) {
            throw new IllegalArgumentException("Invalid clock-in or clock-out time.");
        }
        Duration duration = Duration.between(clockInTime, clockOutTime);
        return duration.toMinutes() / 60.0; // Chuyển đổi từ phút sang giờ
    }
    private String calculateFormattedWorkHours(AttendanceRecord attendanceRecord) {
        LocalDateTime clockInTime = attendanceRecord.getClockInTime();
        LocalDateTime clockOutTime = attendanceRecord.getClockOutTime();

        if (clockInTime == null || clockOutTime == null) {
            return "N/A";
        }

        long minutesWorked = Duration.between(clockInTime, clockOutTime).toMinutes();
        long hours = minutesWorked / 60;
        long minutes = minutesWorked % 60;

        return String.format("%dh%dm", hours, minutes);
    }
    private Shift determineCurrentShift() {
        LocalTime now = LocalTime.now();
        return shiftRepository.findAll().stream()
                .filter(shift -> isWithinShift(now, shift))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active shift found for current time"));
    }

    /**
     * Kiểm tra thời gian có nằm trong ca làm việc hay không
     */
    private boolean isWithinShift(LocalTime time, Shift shift) {
        if (shift.getStartTime().isBefore(shift.getEndTime())) {
            return !time.isBefore(shift.getStartTime()) && !time.isAfter(shift.getEndTime());
        } else {
            return !time.isBefore(shift.getStartTime()) || !time.isAfter(shift.getEndTime());
        }
    }
    public List<AttendanceRecord> getAttendanceRecordsByAccountIdAndDateRange(int accountId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByAccountIDAndDateBetween(accountId, startDate, endDate);
    }
}
