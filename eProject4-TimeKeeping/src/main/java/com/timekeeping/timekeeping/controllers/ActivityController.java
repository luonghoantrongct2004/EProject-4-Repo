package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

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
    public String saveActivity(@ModelAttribute("activity") Activity activity) {
        Optional<Activity> existingActivity = activityService.getActivityById(activity.getActivityId());

        if (existingActivity.isPresent()) {
            activityService.saveActivity(activity);
        } else {
            Activity activityCreate = activityService.saveActivity(activity);
            activityService.sendEmail(activityCreate);
        }
        return "redirect:/activities";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivity(@PathVariable int id) {
        activityService.deleteActivity(id);
        return "redirect:/activities";
    }

    @GetMapping("/find")
    public String findByActivityName(@RequestParam("activityName") String activityName, Model model) {
        model.addAttribute("activities", activityService.findByActivityName(activityName));
        return "activities/index";
    }

    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        // Lấy danh sách các thông báo (đây là ví dụ, bạn có thể lấy từ cơ sở dữ liệu)
        List<Activity> notifications = activityService.getAllActivities();
//                List.of(
//                new ActivityNotification("Họp mặt cuối năm", "Buổi họp mặt sẽ diễn ra vào 18h ngày 31/12", "31/12/2024"),
//                new ActivityNotification("Khóa học kỹ năng mềm", "Khóa học bắt đầu từ ngày 05/01/2025", "05/01/2025"),
//                new ActivityNotification("Team Building", "Hoạt động team building diễn ra ngày 10/02/2025", "10/02/2025")
//        );

        // Đưa dữ liệu vào model để hiển thị trong trang Thymeleaf
        model.addAttribute("notifications", notifications);
        return "activities/list"; // Tên trang HTML sẽ là notificationPage.html
    }

    @GetMapping("/view/{id}")
    public String viewNotifications(@CookieValue(value = "ACCOUNT-ID", defaultValue = "0") int accountID,
                                    @PathVariable int id, Model model){
        Optional<Activity> activity = activityService.getActivityById(id);

        if (activity.isPresent()) {
            model.addAttribute("activity", activity.get());
            model.addAttribute("accountID", accountID);
            return "activities/view";
        }
        return "redirect:/activities";
    }


}