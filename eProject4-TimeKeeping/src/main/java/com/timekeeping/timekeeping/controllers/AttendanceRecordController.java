package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.AttendanceRecord;
import com.timekeeping.timekeeping.services.AttendanceService;
import com.timekeeping.timekeeping.services.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceRecordController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/clockIn")
    public String clockIn(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int accountID = userDetails.getAccount().getAccountID();

            try {
                // Record attendance

//                int accountID = 0;
                attendanceService.clockIn(accountID);
                String name = userDetails.getAccount().getFullName();
                LocalDateTime now = LocalDateTime.now();
                String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<AttendanceRecord> records = attendanceService.getAttendanceRecordsByAccountId(accountID);

                // Add attributes to redirect
                redirectAttributes.addFlashAttribute("name", name);
                redirectAttributes.addFlashAttribute("time", time);
                redirectAttributes.addFlashAttribute("date", date);
                redirectAttributes.addFlashAttribute("attendanceRecords", records);

                return "redirect:/attendance/show?accountID=" + accountID;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error during clock-in: " + e.getMessage());
                return "redirect:/attendance/error";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "User details not found.");
            return "redirect:/attendance/error";
        }
    }

    @GetMapping("/attendance-success")
    public String attendanceSuccess(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            int accountID = userDetails.getAccount().getAccountID();

            List<AttendanceRecord> attendanceRecords = attendanceService.getAttendanceRecordsByAccountId(accountID);
            model.addAttribute("attendanceRecords", attendanceRecords);
            model.addAttribute("accountID", accountID);
            return "home/attendance-success";
        } else {
            model.addAttribute("error", "User details not found.");
            return "error"; // Ensure this matches your error template
        }
    }

    @PostMapping("/clockOut")
    public String clockOut(@RequestParam int recordID, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.clockOut(recordID);
            return "redirect:/attendance/attendance-success";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Record not found: " + e.getMessage());
            return "redirect:/attendance/error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during clock-out: " + e.getMessage());
            return "redirect:/attendance/error";
        }
    }

    @GetMapping("/show")
    public String showAttendanceRecords(@RequestParam(value = "accountID", required = false) Integer accountID,
                                        @RequestParam(value = "dateRange", required = false) String dateRange,
                                        Model model) {
        if (accountID == null || accountID < 0) {
            model.addAttribute("error", "Invalid or missing account ID");
            return "error"; // The name of your error view
        }

        List<AttendanceRecord> attendanceRecords = attendanceService.getAttendanceRecordsByAccountId(accountID);

        model.addAttribute("attendanceRecords", attendanceRecords);
        model.addAttribute("accountID", accountID);

        return "home/attendace-show"; // Thy

    }


        @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("error", "An unexpected error occurred.");
        return "error";
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
            model.addAttribute("error", "Required parameter is missing: " + ex.getParameterName());
            return "error";
        }

        @ExceptionHandler(Exception.class)
        public String handleGeneralException(Exception ex, Model model) {
            model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            return "error";
        }
    }

    private List<AttendanceRecord> getAllAttendanceRecordsForEmployee(int employeeId) {
        return attendanceService.getAttendanceRecordsByAccountId(employeeId);
    }
}
