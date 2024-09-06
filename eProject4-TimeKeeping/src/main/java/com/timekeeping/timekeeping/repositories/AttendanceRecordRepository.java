/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    List<AttendanceRecord> findByAccount_AccountID(int accountID);
    List<AttendanceRecord> findByAccount(Account account);
    List<AttendanceRecord> findByAccountID(int accountID);
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.accountID = :accountID ORDER BY ar.clockInTime DESC")
    AttendanceRecord findLatestByAccountId(@Param("accountID") int accountID);



    // Fetch records by account ID and date range
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.accountID = :accountID AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByAccountIDAndDateBetween(@Param("accountID") int accountID,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);


    List<AttendanceRecord> findByAccountIDAndDate(int accountID, LocalDate date);

    Optional<AttendanceRecord> findByAccountIDAndDateAndStatus(int accountID, LocalDate date, String status);

}