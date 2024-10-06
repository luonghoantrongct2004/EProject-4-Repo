package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
    List<WorkSchedule> findByFullName(@Param("fullName") String fullName);

    @Query("SELECT w FROM WorkSchedule w WHERE w.date BETWEEN :startDate AND :endDate")
    List<WorkSchedule> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT w FROM WorkSchedule w WHERE w.shift.shiftId = :shiftId AND w.date = :date AND w.status = 'APPROVED'")
    List<WorkSchedule> findScheduleForShiftAndDate(@Param("shiftId") int shiftId, @Param("date") LocalDate date);
}