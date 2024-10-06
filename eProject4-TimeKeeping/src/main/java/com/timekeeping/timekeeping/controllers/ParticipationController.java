package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.enums.ParticipationStatus;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.ActivityNotification;
import com.timekeeping.timekeeping.models.Participation;
import com.timekeeping.timekeeping.services.AccountService;
import com.timekeeping.timekeeping.services.ActivityNotificationService;
import com.timekeeping.timekeeping.services.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/participations")
public class ParticipationController {

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActivityNotificationService activityNotificationService;

    @GetMapping
    public String listParticipations(Model model,
                                     @RequestParam(value = "fullName", required = false) String fullName,
                                     @RequestParam(value = "activityName", required = false) String activityName) {
        List<Participation> participations;

        if (fullName != null && !fullName.isEmpty() && activityName != null && !activityName.isEmpty()) {
            participations = participationService.findByAccountNameAndActivityName(fullName, activityName);
        } else if (fullName != null && !fullName.isEmpty()) {
            participations = participationService.findByAccountName(fullName);
        } else if (activityName != null && !activityName.isEmpty()) {
            participations = participationService.findByActivityName(activityName);
        } else {
            participations = participationService.findAll();
        }

        model.addAttribute("participations", participations);
        model.addAttribute("pageTitle", "Participation List");
        return "participations/index";
    }

    @GetMapping("/create")
    public String createParticipationForm(Model model) {
        model.addAttribute("participation", new Participation());
        model.addAttribute("pageTitle", "Create Participation");
        model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
        model.addAttribute("activityNotification", new ActivityNotification());
        model.addAttribute("accounts", accountService.findAllEmployees());
        model.addAttribute("account", new Account());
        return "participations/create";
    }

    @PostMapping("/create")
    public String createParticipation(@ModelAttribute Participation participation, RedirectAttributes redirectAttributes) {
        participation.setReason("");
        participationService.save(participation);
        redirectAttributes.addFlashAttribute("successMessage", "Participation saved successfully!");
        return "redirect:/participations";
    }

    @GetMapping("/edit/{id}")
    public String editParticipationForm(@PathVariable int id, Model model) {
        Optional<Participation> participation = participationService.findById(id);
        if (participation.isPresent()) {
            model.addAttribute("participation", participation.get());
            model.addAttribute("pageTitle", "Edit Participation");
            model.addAttribute("activityNotifications", activityNotificationService.getAllNotifications());
            model.addAttribute("activityNotification", participation.get().getActivityNotification());
            model.addAttribute("accounts", accountService.findAllEmployees());
            model.addAttribute("account", participation.get().getAccount());
            return "participations/edit";
        } else {
            return "redirect:/participations";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateParticipation(@PathVariable int id, @ModelAttribute Participation participation, RedirectAttributes redirectAttributes) {
        participation.setParticipateId(id);
        participationService.save(participation);
        redirectAttributes.addFlashAttribute("successMessage", "Participation saved successfully!");
        return "redirect:/participations";
    }

    @GetMapping("/delete/{id}")
    public String deleteParticipation(@PathVariable int id) {
        participationService.deleteById(id);
        return "redirect:/participations";
    }

    @GetMapping("/approve")
    public String approveParticipation(@RequestParam("id") int id, @RequestParam("status") ParticipationStatus status) {
        Participation participation = participationService.findById(id).orElseThrow();
        participation.setStatus(status);
        participationService.save(participation);
        return "redirect:/participations";
    }
}

