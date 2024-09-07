package com.timekeeping.timekeeping.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeControllerAPI {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/yearly")
    public List<Integer> getEmployeeCountByYear() {
        // Lấy năm hiện tại
        int currentYear = Year.now().getValue();

        // Truy vấn số lượng nhân sự theo từng năm
        List<Object[]> results = entityManager.createQuery(
                        "SELECT YEAR(e.hireDate), COUNT(e) FROM Account e WHERE YEAR(e.hireDate) BETWEEN :startYear AND :endYear GROUP BY YEAR(e.hireDate)")
                .setParameter("startYear", currentYear)
                .setParameter("endYear", 2030)
                .getResultList();

        // Tạo danh sách với số lượng nhân sự mặc định là 0 cho mỗi năm từ năm hiện tại đến 2030
        List<Integer> employeeCounts = new ArrayList<>();
        for (int year = currentYear; year <= 2030; year++) {
            employeeCounts.add(0); // Mặc định là 0 cho mỗi năm
        }

        // Cập nhật danh sách dựa trên dữ liệu trả về từ truy vấn
        for (Object[] result : results) {
            int year = (int) result[0];
            int count = ((Number) result[1]).intValue();
            employeeCounts.set(year - currentYear, count); // Cập nhật số lượng nhân sự cho năm tương ứng
        }

        return employeeCounts;
    }
}
