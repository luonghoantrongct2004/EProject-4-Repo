package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.models.Payroll;
import com.timekeeping.timekeeping.services.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PayrollService payrollService;
    @GetMapping
    public String dashboard(Model model){
        // Lấy số lượng nhân viên
        long totalEmployees = (long) entityManager.createQuery("SELECT COUNT(e) FROM Account e").getSingleResult();

        // Lấy số lượng payroll
        long totalPayrolls = (long) entityManager.createQuery("SELECT COUNT(p) FROM Payroll p").getSingleResult();

        // Lấy số lượng role
        long totalRoles = (long) entityManager.createQuery("SELECT COUNT(r) FROM Role r").getSingleResult();

        // Lấy số lượng chấm công
        long totalAttendances = (long) entityManager.createQuery("SELECT COUNT(a) FROM AttendanceRecord a").getSingleResult();

        // Đưa dữ liệu vào model để hiển thị trên view
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalPayrolls", totalPayrolls);
        model.addAttribute("totalRoles", totalRoles);
        model.addAttribute("totalAttendances", totalAttendances);
        List<Payroll> payrolls = payrollService.findAllPayrolls();

        model.addAttribute("payrolls", payrolls);
        return "dashboard/dashboard";  // Chuyển tới trang dashboard
    }
}
