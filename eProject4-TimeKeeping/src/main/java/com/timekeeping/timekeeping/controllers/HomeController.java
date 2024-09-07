package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Requestion;
import com.timekeeping.timekeeping.models.Shift;
import com.timekeeping.timekeeping.models.WorkSchedule;
import com.timekeeping.timekeeping.services.AccountService;
import com.timekeeping.timekeeping.services.RequestionService;
import com.timekeeping.timekeeping.services.ShiftService;
import com.timekeeping.timekeeping.services.WorkScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private RequestionService requestionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @GetMapping("/home")
    public String home() {
        return "home/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "home/about";
    }
    
    @GetMapping("/service")
    public String service(Model model) {
        model.addAttribute("title", "Service");
        return "home/services";
    }
    @GetMapping("/requestion-create")
    public String requestion(Model model) {
        model.addAttribute("requestion", new Requestion());
        return "home/requestion-create";
    }

    @PostMapping("/requestion-create")
    public String createRequestion(@ModelAttribute Requestion requestion, Model model) {
        requestion.setRequestDate(new Date());
        requestion.setStatus("Dang chờ phê duyệt");
        requestionService.saveRequestion(requestion);
        model.addAttribute("successMessage", "Yêu cầu được gửi thành công");
        return "redirect:/requestion-create";

    }
    @GetMapping("/job")
    public String blog(Model model) {
        model.addAttribute("title", "Job");
        return "home/job";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact");
        return "home/contact";
    }

    @GetMapping("/faceid")
    public String showFaceIdPage() {
        return "home/faceid"; // Ensure this matches the template name
    }
    @GetMapping("/attendance-success")
    public String attendanceSuccess(
            @RequestParam("name") String name,
            @RequestParam("age") String age,
            @RequestParam("time") String time,
            @RequestParam("date") String date,
            Model model) {

        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("time", time);
        model.addAttribute("date", date);

        return "home/attendance-success";
    }

    @GetMapping("/job-list")
    public String showJobPage() {
        return "home/job-list"; // Ensure this matches the template name
    }

    @GetMapping("/viewWorkSchedules")
    public String viewWeeklySchedule(
            @RequestParam(value = "week", required = false) String week,
            @RequestParam(value = "name", required = false) String name,
            Model model) {
        List<Account> accounts;
        LocalDate startOfWeek;
        List<Shift> shifts = shiftService.getAllShifts();
        if (week != null && !week.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_WEEK_DATE;
            startOfWeek = LocalDate.parse(week + "-1", formatter);  // Assumes week format yyyy-Www
        } else {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            startOfWeek = LocalDate.now().with(weekFields.dayOfWeek(), 2);
        }

        if (name != null && !name.isEmpty()) {
            accounts = accountService.findByNameEmployee(name);
            model.addAttribute("accounts", accountService.findByNameEmployee(name));
        } else {
            accounts = accountService.findAllEmployees();
            model.addAttribute("accounts", accountService.findAllEmployees());
        }

        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<WorkSchedule> weeklySchedules = workScheduleService.getSchedulesForWeek(startOfWeek, endOfWeek);

        List<String> dayNamesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1))
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("vi"))
                        + "   \n   " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());

        List<LocalDate> datesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1)).collect(Collectors.toList());

        Map<String, String> dateWithDayName = new LinkedHashMap<>();
        for (LocalDate date : datesInWeek) {
            dateWithDayName.put(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("vi")) , date.format(DateTimeFormatter.ofPattern("dd-MM")));
        }

        model.addAttribute("dateWithDayName", dateWithDayName);
        model.addAttribute("datesInWeek", datesInWeek);
        model.addAttribute("weeklySchedules", weeklySchedules);
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("shift", new Shift());
        model.addAttribute("workSchedule", new WorkSchedule());

        Map<Shift, Map<LocalDate, List<WorkSchedule>>> scheduleMap = new HashMap<>();

        for (Shift shift : shifts) {
            Map<LocalDate, List<WorkSchedule>> dateScheduleMap = new HashMap<>();
            for (LocalDate date : datesInWeek) {
                // Lọc schedule tương ứng với shift và ngày cụ thể
                List<WorkSchedule> schedules = workScheduleService.findScheduleForShiftAndDate(shift.getShiftId(), date);
                dateScheduleMap.put(date, schedules);
            }
            scheduleMap.put(shift, dateScheduleMap);
        }

        model.addAttribute("scheduleMap", scheduleMap);
        return "workSchedules/view";
    }

    @GetMapping("/registerWorkSchedules")
    public String registerSchedule(@CookieValue(value = "ACCOUNT-ID", defaultValue = "0") int accountID
            ,@RequestParam(value = "week", required = false) String week,
                                   Model model) {
        LocalDate startOfWeek;
        Account account = accountService.findById(accountID).orElseThrow();


        if (week != null && !week.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_WEEK_DATE;
            startOfWeek = LocalDate.parse(week + "-1", formatter);  // Assumes week format yyyy-Www
        } else {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            startOfWeek = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<WorkSchedule> weeklySchedules = workScheduleService.getSchedulesForWeek(startOfWeek, endOfWeek);

        List<String> dayNamesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1))
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("vi"))
                        + "   \n   " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());

        List<LocalDate> datesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1)).collect(Collectors.toList());

        Map<String, String> dateWithDayName = new LinkedHashMap<>();
        for (LocalDate date : datesInWeek) {
            dateWithDayName.put(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("vi")) , date.format(DateTimeFormatter.ofPattern("dd-MM")));
        }

        model.addAttribute("dateWithDayName", dateWithDayName);
        model.addAttribute("dayNamesInWeek", dayNamesInWeek);
        model.addAttribute("datesInWeek", datesInWeek);
        model.addAttribute("weeklySchedules", weeklySchedules);
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("shift", new Shift());
        model.addAttribute("workSchedule", new WorkSchedule());

        Map<LocalDate, WorkSchedule> scheduleMap = new LinkedHashMap<>();

        for (LocalDate date : datesInWeek) {
            // Lọc schedule tương ứng với account và ngày cụ thể
            WorkSchedule schedule = workScheduleService.findScheduleForAccountAndDate(account, date, weeklySchedules);
            scheduleMap.put(date, schedule);
        }

        model.addAttribute("scheduleMap", scheduleMap);
        model.addAttribute("acc", account);
        return "workSchedules/register";
    }
}
