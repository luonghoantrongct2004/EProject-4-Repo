package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findByShiftName(@Param("shiftName") String shiftName);
}