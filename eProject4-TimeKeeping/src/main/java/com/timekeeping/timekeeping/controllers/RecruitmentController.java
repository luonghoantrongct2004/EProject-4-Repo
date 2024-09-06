package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Notification;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.services.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recruitments")
public class RecruitmentController {
    @Autowired
    private RecruitmentService recruitmentService;



    @GetMapping("/list")
    public String listRecruitments(Model model) {
        List<Recruitment> recruitments = recruitmentService.findAll();
        model.addAttribute("recruitments", recruitments);
        return "recruitments/recruitment-list";
    }

    @GetMapping("/new")
    public String showRecruitmentForm(Model model) {
        model.addAttribute("recruitments", new Recruitment());
        return "recruitments/recruitment-schedule";
    }




    @PostMapping("/schedule")
    public String scheduleRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
        System.out.println("Received recruitment: " + recruitment);

        // Check if accountID is already associated with a recruitment
        if (recruitmentService.existsByAccountID(recruitment.getAccountID())) {
            model.addAttribute("error", "Recruitment for this account ID already exists.");
            return "recruitments/recruitment-schedule"; // Replace with your actual form view name
        }

        // Save the recruitment information to the database
        recruitmentService.save(recruitment);

        // Redirect to the recruitment list page to display the updated information
        return "redirect:/recruitments/list";
    }




    @GetMapping("/edit/{recruitmentID}")
    public String getRecruitment(@PathVariable int recruitmentID, Model model) {
        Optional<Recruitment> recruitment = recruitmentService.findById(recruitmentID);
        model.addAttribute("recruitment", recruitment);
        return "recruitments/recruitment-edit";
    }


    @PostMapping("/update")
    public String updateRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
        // Check if another recruitment entry has the same accountID
        Recruitment existingRecruitment = recruitmentService.findByAccountID(recruitment.getAccountID());

        if (existingRecruitment != null && existingRecruitment.getRecruitmentID() != recruitment.getRecruitmentID()) {
            // A different recruitment entry with the same accountID exists
            model.addAttribute("error", "Another recruitment with this account ID already exists.");
            return "recruitments/recruitment-edit"; // Ensure this file exists in src/main/resources/templates/
        }

        // Save the updated recruitment information
        recruitmentService.save(recruitment);

        // Redirect to the recruitment list page after updating
        return "redirect:/recruitments/list";
    }

    @GetMapping("/delete/{recruitmentID}")
    public String deleteRecruitment(@PathVariable int recruitmentID) {
        recruitmentService.deleteById(recruitmentID);
        return "redirect:/recruitments/list";
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "test"; // Ensure there is a "test.html" template
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}