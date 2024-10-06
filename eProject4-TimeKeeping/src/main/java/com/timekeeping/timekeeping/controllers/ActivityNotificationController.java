package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.ActivityNotification;
import com.timekeeping.timekeeping.services.AccountService;
import com.timekeeping.timekeeping.services.ActivityNotificationService;
import com.timekeeping.timekeeping.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/activityNotifications")
public class ActivityNotificationController {

    @Autowired
    private ActivityNotificationService activityNotificationService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    AccountService accountService;

    @GetMapping
    public String listNotifications(Model model, @RequestParam(value = "activityName", required = false) String activityName) {
        List<ActivityNotification> notifications;
        if (activityName != null && !activityName.isEmpty()) {
            notifications = activityNotificationService.findByActivityName(activityName);
        } else {
            notifications = activityNotificationService.findAll();
        }
        model.addAttribute("notifications", notifications);
        model.addAttribute("pageTitle", "Activity Notifications");
        return "notifications/index";
    }

    @GetMapping("/create")
    public String createNotificationForm(Model model) {
        model.addAttribute("notification", new ActivityNotification());
        model.addAttribute("pageTitle", "Create Notification");
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("activity", new Activity());
        model.addAttribute("accounts", accountService.findAllEmployees());
        model.addAttribute("account", new Account());
        return "notifications/create";
    }

    @PostMapping("/create")
    public String createNotification(@ModelAttribute ActivityNotification notification,
                                     @RequestParam("date") String date,
                                     @RequestParam("time") String time, RedirectAttributes redirectAttributes) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(time);

        LocalDateTime activityNotificationDateTime = LocalDateTime.of(localDate, localTime);

        notification.setNotificationTime(activityNotificationDateTime);
        notification.setRead(false);
        activityNotificationService.save(notification);
        redirectAttributes.addFlashAttribute("successMessage", "Activity Notification saved successfully!");
        return "redirect:/activityNotifications";
    }

    @GetMapping("/edit/{id}")
    public String editNotificationForm(@PathVariable Long id, Model model) {
        Optional<ActivityNotification> notification = activityNotificationService.findById(id);
        if (notification.isPresent()) {
            model.addAttribute("notification", notification.get());
            model.addAttribute("pageTitle", "Edit Notification");
            model.addAttribute("activities", activityService.getAllActivities());
            model.addAttribute("activity", notification.get().getActivity());
            model.addAttribute("accounts", accountService.findAllEmployees());
            model.addAttribute("account", notification.get().getAccount());
            return "notifications/edit";
        } else {
            return "redirect:/activityNotifications";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateNotification(@PathVariable Long id, @ModelAttribute ActivityNotification notification,
                                     @RequestParam("isRead") boolean isRead,
                                     @RequestParam("date") String date,
                                     @RequestParam("time") String time,
                                     RedirectAttributes redirectAttributes) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(time);

        LocalDateTime activityNotificationDateTime = LocalDateTime.of(localDate, localTime);

        notification.setNotificationTime(activityNotificationDateTime);
        notification.setId(id);
        notification.setRead(isRead);
        activityNotificationService.save(notification);
        redirectAttributes.addFlashAttribute("successMessage", "Activity Notification saved successfully!");
        return "redirect:/activityNotifications";
    }

    @GetMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id, Model model) {
        try {
            activityNotificationService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "This Notification is being referenced by records in the Participation table. Please delete relevant data before deleting this Notification! (Remove Participation -> Notifications");
            model.addAttribute("notifications", activityNotificationService.findAll());
            model.addAttribute("pageTitle", "Activity Notifications");
            return "notifications/index";
        }
        return "redirect:/activityNotifications";
    }
}
