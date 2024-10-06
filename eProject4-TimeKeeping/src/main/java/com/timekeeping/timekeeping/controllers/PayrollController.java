package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.models.Payroll;
import com.timekeeping.timekeeping.models.SalaryTemplate;
import com.timekeeping.timekeeping.services.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;
    @GetMapping("/generate-payroll")
    public String generatePayroll() {
        List<Account> accounts = payrollService.getAllAccounts();
        for (Account account : accounts) {
            List<AttendanceRecord> attendances = payrollService.findAttendancesByAccountAndMonth(account, LocalDate.now());
            payrollService.generatePayroll(account, attendances);
        }
        return "payroll/index";
    }
    @GetMapping
    public String getAllPayrolls(@RequestParam(value = "name", required = false) String name, Model model) {
        List<Payroll> payrolls;

        if (name != null && !name.isEmpty()) {
            payrolls = payrollService.findByName(name);
        } else {
            payrolls = payrollService.findAllPayrolls();
        }

        System.out.println("Found " + payrolls.size() + " payroll records.");

        model.addAttribute("payrolls", payrolls);
        return "payroll/index";
    }

    @GetMapping("/{id}")
    public String getPayroll(@PathVariable int id, Model model) {
        Optional<Payroll> payroll = Optional.ofNullable(payrollService.findPayrollById(id));
        if (payroll.isPresent()) {
            model.addAttribute("payroll", payroll.get());
            return "payroll/detail";
        } else {
            return "redirect:/payroll";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPayrollForm(@PathVariable int id, Model model) {
        Optional<Payroll> payroll = Optional.ofNullable(payrollService.findPayrollById(id));
        if (payroll.isPresent()) {
            model.addAttribute("payroll", payroll.get());
            return "payroll/edit";
        } else {
            return "redirect:/payroll";
        }
    }

    @PostMapping("/edit")
    public String editPayroll(@ModelAttribute Payroll payroll) {
        payrollService.updatePayroll(payroll);
        return "redirect:/payroll";
    }
}
