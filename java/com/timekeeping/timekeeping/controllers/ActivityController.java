package com.timekeeping.timekeeping.controllers;

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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

   @Autowired
   private AccountService accountService;

   @Autowired
   private ActivityNotificationService activityNotificationService;

    @GetMapping
    public String getAllActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("activity", new Activity());
        return "activities/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("activity", new Activity());
        return "activities/create";
    }

    @GetMapping("/edit/{id}")
    public String editActivity(@PathVariable int id, Model model) {
        Optional<Activity> activity = activityService.getActivityById(id);
        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());
            return "activities/edit";
        }
        return "redirect:/activities";
    }

    @PostMapping
    public String saveActivity(@ModelAttribute("activity") Activity activity,
                               @RequestParam("date") String date,
                               @RequestParam("time") String time,
                               @CookieValue(value = "ACCOUNT-ID", defaultValue = "0") int accountID,
                               RedirectAttributes redirectAttributes) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(time);

        // Combine date and time into LocalDateTime
        LocalDateTime activityDateTime = LocalDateTime.of(localDate, localTime);

        // Set the activityDateTime into activity object
        activity.setStartTime(activityDateTime);

        Optional<Activity> existingActivity = activityService.getActivityById(activity.getActivityId());

        if (existingActivity.isPresent()) {
            activityService.saveActivity(activity);
        } else {
            Activity activityCreate = activityService.saveActivity(activity);
            ActivityNotification activityNotification = new ActivityNotification();
            activityNotification.setAccount(accountService.findById(accountID).orElseThrow());
            activityNotification.setActivity(activityCreate);
            String mandatory = String.valueOf(activityCreate.getType());
            activityNotification.setContent("[" + mandatory + "] " + activityCreate.getActivityName() + " - Confirm Your Attendance");
            activityNotification.setNotificationTime(LocalDateTime.now());
            activityNotification.setRead(false);
            activityNotificationService.saveNotification(activityNotification);
            activityService.sendEmail(activityCreate, activityNotification.getId());
        }
        redirectAttributes.addFlashAttribute("successMessage", "Activity saved successfully!");
        return "redirect:/activities";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivity(@PathVariable int id, Model model) {
        try {
            activityService.deleteActivity(id);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "This activity is being referenced by records in the activity_notification and participation tables. Please delete relevant data before deleting this activity! (Delete Participation -> Notification -> Activity)");
            model.addAttribute("activities", activityService.getAllActivities());
            model.addAttribute("activity", new Activity());
            return "activities/index";
        }
        return "redirect:/activities";
    }

    @GetMapping("/find")
    public String findByActivityName(@RequestParam("activityName") String activityName, Model model) {
        model.addAttribute("activities", activityService.findByActivityName(activityName));
        return "activities/index";
    }
}