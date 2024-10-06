package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.Shift;
import com.timekeeping.timekeeping.services.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/shifts")
public class ShiftController {
    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public String getAllShifts(Model model) {
        model.addAttribute("shifts", shiftService.getAllShifts());
        model.addAttribute("shift", new Shift());
        return "shifts/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("shift", new Shift());
        return "shifts/create";
    }

    @GetMapping("/edit/{id}")
    public String editShift(@PathVariable int id, Model model) {
        Optional<Shift> shift = shiftService.getShiftById(id);
        if (shift.isPresent()) {
            model.addAttribute("shift", shift.get());
            return "shifts/edit";
        }
        return "redirect:/shifts";
    }

    @PostMapping
    public String saveShift(@ModelAttribute("shift") Shift shift, RedirectAttributes redirectAttributes) {
        shiftService.saveShift(shift);
        redirectAttributes.addFlashAttribute("successMessage", "Shift saved successfully!");
        return "redirect:/shifts";
    }

    @GetMapping("/delete/{id}")
    public String deleteShift(@PathVariable int id, Model model) {
        try {
            shiftService.deleteShift(id);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "This shift is being referenced by records in the Work Schedules table. Please delete relevant data before deleting this Shift! (Delete Work Schedule -> Shift)");
            model.addAttribute("shifts", shiftService.getAllShifts());
            model.addAttribute("shift", new Shift());
            return "shifts/index";
        }
        return "redirect:/shifts";
    }

    @GetMapping("/find")
    public String findShiftsByName(@RequestParam("shiftName") String shiftName, Model model) {
        List<Shift> shifts = shiftService.findShiftsByName(shiftName);
        model.addAttribute("shifts", shifts);
        model.addAttribute("shift", new Shift());
        return "shifts/index";
    }
}
