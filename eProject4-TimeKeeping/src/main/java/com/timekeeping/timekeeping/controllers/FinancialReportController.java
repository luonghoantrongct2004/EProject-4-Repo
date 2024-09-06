package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.FinancialReport;
import com.timekeeping.timekeeping.services.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/financialReports")
public class FinancialReportController {
    @Autowired
    private FinancialReportService financialReportService;

    @GetMapping
    public String getAllReports(Model model) {
        model.addAttribute("financialReports", financialReportService.getAllReports());
        model.addAttribute("financialReport", new FinancialReport());
        return "financialReports/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("financialReport", new FinancialReport());
        return "financialReports/create";
    }

    @GetMapping("/edit/{id}")
    public String editReport(@PathVariable int id, Model model) {
        Optional<FinancialReport> report = financialReportService.getReportById(id);
        if (report.isPresent()) {
            model.addAttribute("financialReport", report.get());
            return "financialReports/edit";
        }
        return "redirect:/financialReports";
    }

    @PostMapping
    public String saveReport(@ModelAttribute("financialReport") FinancialReport report) {
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(LocalDate.now());
        }
        financialReportService.saveReport(report);
        return "redirect:/financialReports";
    }

    @GetMapping("/delete/{id}")
    public String deleteReport(@PathVariable int id) {
        financialReportService.deleteReport(id);
        return "redirect:/financialReports";
    }

    @GetMapping("/find")
    public String findByTitle(@RequestParam("title") String title, Model model) {
        model.addAttribute("financialReports", financialReportService.findByTitle(title));
        return "financialReports/index";
    }
}