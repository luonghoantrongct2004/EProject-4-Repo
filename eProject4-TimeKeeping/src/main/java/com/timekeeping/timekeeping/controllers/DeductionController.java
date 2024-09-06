package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Deduction;
import com.timekeeping.timekeeping.services.DeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/deduction")
public class DeductionController {
    @Autowired
    private DeductionService deductionService;

    @GetMapping
    public String getAllDeductions(@RequestParam(value = "type", required = false) String type, Model model) {
        List<Deduction> deductions;

        if (type != null && !type.isEmpty()) {
            deductions = deductionService.findByType(type); // Assuming you're searching by deduction type
        } else {
            deductions = deductionService.findAll();
        }

        model.addAttribute("deductions", deductions);
        return "deduction/index"; // Assuming you have a Thymeleaf template named index.html under the "deduction" directory
    }


    @GetMapping("/{id}")
    public String getDeductionById(@PathVariable int id, Model model) {
        Optional<Deduction> deduction = Optional.ofNullable(deductionService.findById(id));
        if (deduction.isPresent()) {
            model.addAttribute("deduction", deduction.get());
            return "deduction/detail"; // Assuming you have a Thymeleaf template named detail.html under the "deductions" directory
        } else {
            return "redirect:/deduction"; // Redirect if the deduction is not found
        }
    }

    @GetMapping("/create")
    public String createDeduction(Model model) {
        model.addAttribute("deduction", new Deduction());
        return "deduction/create"; // Assuming you have a Thymeleaf template named create.html under the "deductions" directory
    }

    @PostMapping("/create")
    public String createDeduction(@ModelAttribute Deduction deduction) {
        deductionService.save(deduction);
        return "redirect:/deduction";
    }

    @GetMapping("/edit/{id}")
    public String editDeduction(@PathVariable int id, Model model) {
        Optional<Deduction> deduction = Optional.ofNullable(deductionService.findById(id));
        if (deduction.isPresent()) {
            model.addAttribute("deduction", deduction.get());
            return "deduction/edit";
        } else {
            return "redirect:/deduction";
        }
    }

    @PostMapping("/edit")
    public String editDeduction(@ModelAttribute Deduction deduction) {
        deductionService.save(deduction);
        return "redirect:/deduction";
    }

    @GetMapping("/delete/{id}")
    public String deleteDeduction(@PathVariable int id) {
        deductionService.delete(id);
        return "redirect:/deduction";
    }
}
