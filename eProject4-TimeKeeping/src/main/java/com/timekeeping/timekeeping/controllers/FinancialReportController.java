package com.timekeeping.timekeeping.controllers;

//import com.timekeeping.timekeeping.models.FinancialReport;
//import com.timekeeping.timekeeping.services.FinancialReportService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.Optional;

//@Controller
//@RequestMapping("/financialReports")
//public class FinancialReportController {
//    @Autowired
//    private FinancialReportService financialReportService;
//
//    @GetMapping
//    public String getAllReports(Model model) {
//        model.addAttribute("financialReports", financialReportService.getAllReports());
//        model.addAttribute("financialReport", new FinancialReport());
//        return "financialReports/index";
//    }
//
//    @GetMapping("/create")
//    public String create(Model model) {
//        model.addAttribute("financialReport", new FinancialReport());
//        return "financialReports/create";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editReport(@PathVariable int id, Model model) {
//        Optional<FinancialReport> report = financialReportService.getReportById(id);
//        if (report.isPresent()) {
//            model.addAttribute("financialReport", report.get());
//            return "financialReports/edit";
//        }
//        return "redirect:/financialReports";
//    }
//
//    @PostMapping
//    public String saveReport(@ModelAttribute("financialReport") FinancialReport report) {
//        if (report.getCreatedAt() == null) {
//            report.setCreatedAt(LocalDate.now());
//        }
//        financialReportService.saveReport(report);
//        return "redirect:/financialReports";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteReport(@PathVariable int id) {
//        financialReportService.deleteReport(id);
//        return "redirect:/financialReports";
//    }
//
//    @GetMapping("/find")
//    public String findByTitle(@RequestParam("title") String title, Model model) {
//        model.addAttribute("financialReports", financialReportService.findByTitle(title));
//        return "financialReports/index";
//    }
//}

import com.timekeeping.timekeeping.models.FinancialReport;
import com.timekeeping.timekeeping.services.AccountService;
import com.timekeeping.timekeeping.services.FinancialReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/financialReports")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    @Autowired
    private AccountService accountService;

    @GetMapping
    public String viewFinancialReports(Model model) {
        List<FinancialReport> reports = financialReportService.getAllReports();
        model.addAttribute("reports", reports);
        return "financialReports/financial_reports"; // tên file Thymeleaf
    }

    @RequestMapping("/financial-report/{id}")
    public String viewFinancialReportDetail(@PathVariable("id") int id, Model model) {
        FinancialReport report = financialReportService.getReportById(id).orElseThrow();
        model.addAttribute("report", report);
        model.addAttribute("accounts", accountService.findAll());
        return "financialReports/financial_report_detail";
    }

    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=financial_report.xlsx";
        response.setHeader(headerKey, headerValue);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Reports");

        // Tạo tiêu đề cho báo cáo
        Row reportTitleRow = sheet.createRow(0);
        Cell reportTitleCell = reportTitleRow.createCell(0);
        reportTitleCell.setCellValue("BÁO CÁO TÀI CHÍNH");

        // Tạo thông tin chung
        Row infoRow1 = sheet.createRow(2);
        infoRow1.createCell(0).setCellValue("Thời gian báo cáo:");
        infoRow1.createCell(1).setCellValue("Tháng 8/2024");

        Row infoRow2 = sheet.createRow(3);
        infoRow2.createCell(0).setCellValue("Người lập báo cáo:");
        infoRow2.createCell(1).setCellValue("Hoàn Trọng");

        Row infoRow3 = sheet.createRow(4);
        infoRow3.createCell(0).setCellValue("Ngày lập:");
        infoRow3.createCell(1).setCellValue("2024-08-31");

        // Tạo tiêu đề bảng
        Row headerRow = sheet.createRow(6);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("Mã nhân viên");
        headerRow.createCell(2).setCellValue("Tên nhân viên");
        headerRow.createCell(3).setCellValue("Vị trí");
        headerRow.createCell(4).setCellValue("Lương cơ bản (VND)");
        headerRow.createCell(5).setCellValue("Phụ cấp (VND)");
        headerRow.createCell(6).setCellValue("Tổng lương (VND)");

        // Thêm dữ liệu của nhân viên
        Object[][] employeeData = {
                {1, "1", "Hoàn Trọng", "Vị trí 1", 10000000, 2000000, 12000000},
                {2, "3", "Luong Hoan Trong", "Vị trí 2", 9000000, 1500000, 10500000},
                {3, "4", "Nguyễn Thanh Đăng", "Vị trí 3", 8000000, 1000000, 9000000}
        };

        int rowNum = 7;  // Bắt đầu từ hàng sau tiêu đề
        for (Object[] employee : employeeData) {
            Row row = sheet.createRow(rowNum++);
            for (int colNum = 0; colNum < employee.length; colNum++) {
                row.createCell(colNum).setCellValue(employee[colNum].toString());
            }
        }

        // Tạo dòng tổng cộng lương
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(5).setCellValue("Tổng lương nhân viên:");
        totalRow.createCell(6).setCellValue("31000000 VND");

        // Ghi workbook ra OutputStream
        workbook.write(response.getOutputStream());
        workbook.close();
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
