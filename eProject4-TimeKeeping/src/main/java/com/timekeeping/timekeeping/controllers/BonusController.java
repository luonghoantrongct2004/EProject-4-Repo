package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Bonus;
import com.timekeeping.timekeeping.services.BonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bonus")
public class BonusController {

    @Autowired
    private BonusService bonusService;

    @GetMapping
    public String getAllBonuses(@RequestParam(value = "type", required = false) String type, Model model) {
        List<Bonus> bonuses;

        if (type != null && !type.isEmpty()) {
            bonuses = bonusService.findByType(type); // Assuming you're searching by bonus type
        } else {
            bonuses = bonusService.findAll();
        }

        model.addAttribute("bonuses", bonuses);
        return "bonus/index"; // Assuming you have a Thymeleaf template named index.html under the "bonus" directory
    }

    @GetMapping("/{id}")
    public String getBonusById(@PathVariable int id, Model model) {
        Optional<Bonus> bonus = Optional.ofNullable(bonusService.findById(id));
        if (bonus.isPresent()) {
            model.addAttribute("bonus", bonus.get());
            return "bonus/detail"; // Assuming you have a Thymeleaf template named detail.html under the "bonuses" directory
        } else {
            return "redirect:/bonus"; // Redirect if the bonus is not found
        }
    }

    @GetMapping("/create")
    public String createBonus(Model model) {
        model.addAttribute("bonus", new Bonus());
        return "bonus/create"; // Assuming you have a Thymeleaf template named create.html under the "bonuses" directory
    }

    @PostMapping("/create")
    public String createBonus(@ModelAttribute Bonus bonus) {
        bonusService.save(bonus);
        return "redirect:/bonus";
    }

    @GetMapping("/edit/{id}")
    public String editBonus(@PathVariable int id, Model model) {
        Optional<Bonus> bonus = Optional.ofNullable(bonusService.findById(id));
        if (bonus.isPresent()) {
            model.addAttribute("bonus", bonus.get());
            return "bonus/edit";
        } else {
            return "redirect:/bonus";
        }
    }

    @PostMapping("/edit")
    public String editBonus(@ModelAttribute Bonus bonus) {
        bonusService.save(bonus);
        return "redirect:/bonus";
    }

    @GetMapping("/delete/{id}")
    public String deleteBonus(@PathVariable int id) {
        bonusService.delete(id);
        return "redirect:/bonus";
    }

}
