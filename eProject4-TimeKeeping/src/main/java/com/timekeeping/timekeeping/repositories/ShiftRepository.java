package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findByShiftName(@Param("shiftName") String shiftName);
    @Query("SELECT s FROM Shift s WHERE s.startTime <= :time AND s.endTime >= :time")
    Shift findCurrentShift(@Param("time") LocalTime time);
}
