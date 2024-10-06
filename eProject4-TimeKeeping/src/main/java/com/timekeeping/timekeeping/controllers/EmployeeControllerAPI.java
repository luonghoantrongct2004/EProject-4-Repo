package com.timekeeping.timekeeping.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/employees")
public class EmployeeControllerAPI {

    @PersistenceContext
    private EntityManager entityManager;

    // Employee count by year with optional year filter
    @GetMapping("/yearly")
    public List<Integer> getEmployeeCountByYear(@RequestParam(value = "startYear", required = false) Integer startYear,
                                                @RequestParam(value = "endYear", required = false) Integer endYear) {
        int currentYear = Year.now().getValue();
        startYear = (startYear != null) ? startYear : currentYear;
        endYear = (endYear != null) ? endYear : 2030;

        List<Object[]> results = entityManager.createQuery(
                        "SELECT YEAR(e.hireDate), COUNT(e) FROM Account e WHERE YEAR(e.hireDate) BETWEEN :startYear AND :endYear GROUP BY YEAR(e.hireDate)")
                .setParameter("startYear", startYear)
                .setParameter("endYear", endYear)
                .getResultList();

        List<Integer> employeeCounts = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            employeeCounts.add(0); // Default to 0
        }

        for (Object[] result : results) {
            int year = (int) result[0];
            int count = ((Number) result[1]).intValue();
            employeeCounts.set(year - startYear, count); // Update count for the corresponding year
        }

        return employeeCounts;
    }

    // Salary data filtered by year and month (optional month)
    @GetMapping("/salary/monthlyYearly")
    public List<Object[]> getSalaryByYearAndMonth(@RequestParam(value = "year", required = false) Integer year,
                                                  @RequestParam(value = "month", required = false) Integer month) {
        int currentYear = Year.now().getValue();
        year = (year != null) ? year : currentYear;

        String queryString = "SELECT YEAR(e.hireDate), MONTH(e.hireDate)FROM Account e WHERE YEAR(e.hireDate) = :year";
        if (month != null && month > 0) {
            queryString += " AND MONTH(e.hireDate) = :month";
        }

        Query query = entityManager.createQuery(queryString)
                .setParameter("year", year);

        if (month != null && month > 0) {
            query.setParameter("month", month);
        }

        return query.getResultList();
    }
}
