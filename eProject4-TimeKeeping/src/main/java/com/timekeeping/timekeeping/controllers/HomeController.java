package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.enums.ActivityType;
import com.timekeeping.timekeeping.enums.ApprovalStatus;
import com.timekeeping.timekeeping.enums.ParticipationStatus;
import com.timekeeping.timekeeping.models.*;
import com.timekeeping.timekeeping.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ActivityNotificationService activityNotificationService;

    @Autowired
    private RequestionService requestionService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "home/index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "home/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        model.addAttribute("title", "About");
        return "home/about";
    }
    
    @GetMapping("/service")
    public String service(Model model) {
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        model.addAttribute("title", "Service");
        return "home/services";
    }
    @GetMapping("/requestion-create")
    public String requestion(Model model) {
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        model.addAttribute("requestion", new Requestion());
        return "home/requestion-create";
    }

    @PostMapping("/requestion-create")
    public String createRequestion(@ModelAttribute Requestion requestion, Model model) {
        requestion.setRequestDate(new Date());
        requestion.setStatus("Dang chờ phê duyệt");
        requestionService.saveRequestion(requestion);
        model.addAttribute("successMessage", "Yêu cầu được gửi thành công");
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "redirect:/requestion-create";

    }
    @GetMapping("/job")
    public String blog(Model model) {
        model.addAttribute("title", "Job");
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "home/job";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact");
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
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
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());

        return "home/attendance-success";
    }

    @GetMapping("/job-list")
    public String showJobPage(Model model) {

        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "home/job-list"; // Ensure this matches the template name
    }

    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        // Lấy danh sách các thông báo (đây là ví dụ, bạn có thể lấy từ cơ sở dữ liệu)
        List<ActivityNotification> activityNotifications = activityNotificationService.getAllNotifications();
        model.addAttribute("activityNotifications", activityNotifications);
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
        return "home/listActivities";
    }

    @GetMapping("/notifications/view/{id}")
    public String viewNotifications(@CookieValue(value = "ACCOUNT-ID", defaultValue = "0") int accountID,
                                    @PathVariable Long id, Model model){
        Optional<ActivityNotification> activityNotification = activityNotificationService.getActivityNotificationById(id);

        if (activityNotification.isPresent()) {
            ActivityNotification activityNotificationInstance = activityNotification.get();
            activityNotificationInstance.setRead(true);
            activityNotificationService.saveNotification(activityNotificationInstance);
            model.addAttribute("activityNotification", activityNotificationInstance);
            model.addAttribute("accountID", accountID);
            model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
            model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());
            model.addAttribute("isConfirm", participationService.find(id, accountID));
            boolean isMandatory = activityNotificationInstance.getActivity().getType() == ActivityType.MANDATORY;
            model.addAttribute("isMandatory", isMandatory);

            return "home/viewActivity";
        }
        return "redirect:/home";
    }

    @GetMapping("/activities/participate")
    public String apcetParticipate(@RequestParam("id") int id,
                                   @RequestParam("accountID") int accountID) {
        participationService.confirmParticipation(id, accountID, ParticipationStatus.JOINED, "");
        return "redirect:/home";
    }

    @PostMapping("/activities/participate")
    public String apcetParticipate(@RequestParam("id") int id,
                                   @RequestParam("accountID") int accountID,
                                   @RequestParam("reason") String reason) {
        participationService.confirmParticipation(id, accountID, ParticipationStatus.DENIED, reason);
        return "redirect:/home";
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
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en"))
                        + "   \n   " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());

        List<LocalDate> datesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1)).collect(Collectors.toList());

        Map<String, String> dateWithDayName = new LinkedHashMap<>();
        for (LocalDate date : datesInWeek) {
            dateWithDayName.put(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en")) , date.format(DateTimeFormatter.ofPattern("dd-MM")));
        }

        model.addAttribute("dateWithDayName", dateWithDayName);
        model.addAttribute("datesInWeek", datesInWeek);
        model.addAttribute("weeklySchedules", weeklySchedules);
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("shift", new Shift());
        model.addAttribute("workSchedule", new WorkSchedule());
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());

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
        return "home/viewWorkSchedules";
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
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en"))
                        + "   \n   " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());

        List<LocalDate> datesInWeek = startOfWeek.datesUntil(endOfWeek.plusDays(1)).collect(Collectors.toList());

        Map<String, String> dateWithDayName = new LinkedHashMap<>();
        for (LocalDate date : datesInWeek) {
            dateWithDayName.put(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en")) , date.format(DateTimeFormatter.ofPattern("dd-MM")));
        }

        model.addAttribute("dateWithDayName", dateWithDayName);
        model.addAttribute("dayNamesInWeek", dayNamesInWeek);
        model.addAttribute("datesInWeek", datesInWeek);
        model.addAttribute("weeklySchedules", weeklySchedules);
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("shift", new Shift());
        model.addAttribute("workSchedule", new WorkSchedule());
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityUnreadNotifications", activityNotificationService.getUnreadNotifications());

        Map<LocalDate, WorkSchedule> scheduleMap = new LinkedHashMap<>();

        for (LocalDate date : datesInWeek) {
            // Lọc schedule tương ứng với account và ngày cụ thể
            WorkSchedule schedule = workScheduleService.findScheduleForAccountAndDate(account, date, weeklySchedules);
            scheduleMap.put(date, schedule);
        }

        model.addAttribute("scheduleMap", scheduleMap);
        model.addAttribute("acc", account);
        return "home/registerWorkSchedules";
    }

    @GetMapping("/registerWorkSchedules/updateShift")
    public String updateShiftSchedule(@RequestParam("scheduleId") int scheduleId, @RequestParam("shiftId") int shiftId, @RequestParam(value = "register", required = false) String register) {
        workScheduleService.updateShiftSchedule(scheduleId, shiftId);
        if (register != null && !register.isEmpty()) {
            return "redirect:/registerWorkSchedules";
        }
        return "redirect:/registerWorkSchedules";
    }

    @PostMapping("/registerWorkSchedules/create")
    public String create(@RequestParam("accountId") Integer accountId,
                         @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                         @RequestParam("shiftId") Integer shiftId,
                         @RequestParam(value = "register", required = false) String register,
                         RedirectAttributes redirectAttributes) {
        if (accountId == null || date == null || shiftId == null) {
            throw new IllegalArgumentException("One or more parameters are invalid");
        }
        WorkSchedule newSchedule = new WorkSchedule();
        Account account = accountService.findById(accountId).orElseThrow();
        Shift shift = shiftService.getShiftById(shiftId).orElseThrow();

        newSchedule.setAccount(account);
        newSchedule.setDate(date);
        newSchedule.setShift(shift);
        newSchedule.setStatus(ApprovalStatus.PENDING);

        workScheduleService.saveSchedule(newSchedule);
        redirectAttributes.addFlashAttribute("successMessage", "Work Schedule saved successfully!");

        if (register != null && !register.isEmpty()) {
            return "redirect:/registerWorkSchedules";
        }
        return "redirect:/registerWorkSchedules";
    }
}
