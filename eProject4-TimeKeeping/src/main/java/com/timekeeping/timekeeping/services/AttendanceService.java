package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.repositories.AttendanceRecordRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveAttendanceRecord(AttendanceRecord attendanceRecord) {
        entityManager.persist(attendanceRecord);
    }

    public List<AttendanceRecord> findAttendancesByAccount(Account account) {
        return attendanceRecordRepository.findByAccount(account);
    }

    @Transactional
    public void clockIn(int accountID) {
        if (accountID <= 0) {
            throw new IllegalArgumentException("Invalid account ID");
        }

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setAccountID(accountID);
        attendanceRecord.setDate(LocalDate.now());
        attendanceRecord.setClockInTime(LocalDateTime.now());
        attendanceRecord.setStatus("Clocked In");

        try {
            attendanceRecordRepository.save(attendanceRecord);
        } catch (Exception e) {
            // Use a logger for better error tracking
            LoggerFactory.getLogger(getClass()).error("Error saving attendance record", e);
            throw e; // Rethrow to handle in controller
        }
    }
    @Transactional
    public void clockOut(int accountID) {
        if (accountID <= 0) {
            throw new IllegalArgumentException("Invalid account ID");
        }

        // Uncomment if you need to validate employee existence
        // Employee employee = employeeRepository.findById(accountID)
        //         .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create a new AttendanceRecord for clock-in
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setAccountID(accountID);
        attendanceRecord.setDate(LocalDate.now());
        attendanceRecord.setClockOutTime(LocalDateTime.now());
        attendanceRecord.setStatus("Clocked Out");

        // Save the attendance record
        attendanceRecordRepository.save(attendanceRecord);
    }

    public double calculateTotalWorkingHours(int accountId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceRecord> records = getAttendanceRecordsByAccountIdAndDateRange(accountId, startDate, endDate);
        double totalHours = 0.0;

        for (AttendanceRecord record : records) {
            LocalDateTime clockInTime = record.getClockInTime();
            LocalDateTime clockOutTime = record.getClockOutTime();

            if (clockInTime != null && clockOutTime != null) {
                totalHours += calculateWorkingHours(clockInTime, clockOutTime);
            }
        }

        return totalHours;
    }

    public static double calculateWorkingHours(LocalDateTime clockInTime, LocalDateTime clockOutTime) {
        if (clockInTime == null || clockOutTime == null) {
            return 0; // Handle the case where one or both times are null
        }

        if (clockOutTime.isBefore(clockInTime)) {
            throw new IllegalArgumentException("Clock-out time must be after clock-in time.");
        }

        Duration duration = Duration.between(clockInTime, clockOutTime);
        return duration.toMinutes() / 60.0; // Convert minutes to hours
    }
    public List<AttendanceRecord> getAttendanceRecordsByAccountId(int accountID) {
        return attendanceRecordRepository.findByAccount_AccountID(accountID);
    }
    public Iterable<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceRecordRepository.findAll();
    }

    public List<AttendanceRecord> getDefaultAttendanceRecords() {
        return attendanceRecordRepository.findAll();
    }

    public List<AttendanceRecord> getAttendanceRecordsByAccountIdAndDateRange(int accountID, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByAccountIDAndDateBetween(accountID, startDate, endDate);
    }

    public List<AttendanceRecord> getAttendanceRecordsByAccountIdAndDate(int accountID, LocalDate date) {
        return attendanceRecordRepository.findByAccountIDAndDate(accountID, date);
    }
}
