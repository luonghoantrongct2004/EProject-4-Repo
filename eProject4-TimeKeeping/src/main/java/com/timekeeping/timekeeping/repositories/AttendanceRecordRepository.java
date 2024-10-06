package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.timekeeping.timekeeping.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {

    // Fetch records by account ID
    List<AttendanceRecord> findByAccount_AccountID(int accountID);

    // Fetch records by Account entity
    List<AttendanceRecord> findByAccount(Account account);

    // Fetch records by account ID and specific date
    List<AttendanceRecord> findByAccount_AccountIDAndDate(int accountID, LocalDate date);

    // Fetch records by account ID and date range
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.account.accountID = :accountID AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByAccountIDAndDateBetween(@Param("accountID") int accountID,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    // Fetch the latest attendance record for a specific account ID
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.account.accountID = :accountID ORDER BY ar.clockInTime DESC")
    AttendanceRecord findLatestByAccountId(@Param("accountID") int accountID);

    // Fetch the most recent clock-in record for an account that hasn't clocked out
    Optional<AttendanceRecord> findTopByAccount_AccountIDAndClockOutTimeIsNullOrderByClockInTimeDesc(int accountID);

    // Fetch the last clock-in record for a specific account ID and date
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.account.accountID = :accountID AND ar.date = :date AND ar.clockInTime IS NOT NULL ORDER BY ar.clockInTime DESC")
    Optional<AttendanceRecord> findLastClockInRecordByAccountID(@Param("accountID") int accountID, @Param("date") LocalDate date);

    // Fetch attendance records based on account ID and date with status filtering
    Optional<AttendanceRecord> findByAccount_AccountIDAndDateAndStatus(int accountID, LocalDate date, String status);

    List<AttendanceRecord> findByAccountAccountID(int accountId);

    boolean existsByAccount_AccountIDAndClockOutTimeIsNull(int accountID);

//    Optional<AttendanceRecord> findTopByAccount_AccountIDAndClockOutTimeIsNullOrderByClockInTimeDesc(int accountID);

    //    List<AttendanceRecord> findByAccount_AccountID(int accountID);
    List<AttendanceRecord> findByDate(LocalDate date);

    Optional<AttendanceRecord> findTopByAccountAndDateOrderByClockInTimeDesc(Account account, LocalDate date);

    Optional<AttendanceRecord> findTopByAccountAndStatusOrderByClockInTimeDesc(Account account, String status);


    List<AttendanceRecord> findByAccount_AccountID(Integer accountID);

    /**
     * Tìm tất cả các bản ghi chấm công
     *
     * @return Danh sách tất cả các bản ghi chấm công
     */
    List<AttendanceRecord> findAll();

    List<AttendanceRecord> findByAccountAndDate(Account account, LocalDate date);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.account = :account AND ar.shift = :shift")
    List<AttendanceRecord> findByAccountAndShift(@Param("account") Account account, @Param("shift") Shift shift);


    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.account = :account AND ar.shift = :shift AND ar.date = :date")
    List<AttendanceRecord> findByAccountAndShiftAndDate(@Param("account") Account account, @Param("shift") Shift shift, @Param("date") LocalDate date);


    Optional<AttendanceRecord> findFirstByAccountAndStatusOrderByClockInTimeDesc(Account account, String status);

    Optional<AttendanceRecord> findFirstByAccountOrderByClockInTimeDesc(Account account);



    Optional<AttendanceRecord> findFirstByAccountAndClockOutTimeIsNullOrderByClockInTimeDesc(Account account);

}

