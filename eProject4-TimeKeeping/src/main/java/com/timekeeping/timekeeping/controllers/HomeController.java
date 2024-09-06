package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Requestion;
import com.timekeeping.timekeeping.services.RequestionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private RequestionService requestionService;
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

}
