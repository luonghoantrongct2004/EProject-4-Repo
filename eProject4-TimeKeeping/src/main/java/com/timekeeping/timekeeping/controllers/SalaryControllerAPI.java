package com.timekeeping.timekeeping.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryControllerAPI {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/monthlyYearly")
    public List<Object[]> getMonthlyYearlySalary() {
        return entityManager.createQuery(
                        "SELECT YEAR(s.createdAt), MONTH(s.createdAt), SUM(s.netSalary) FROM Payroll s GROUP BY YEAR(s.createdAt), MONTH(s.createdAt)")
                .getResultList();
    }

}
