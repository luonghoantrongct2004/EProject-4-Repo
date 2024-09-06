package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Integer> {
    List<FinancialReport> findByTitle(@Param("title") String title);
}