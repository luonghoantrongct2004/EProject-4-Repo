package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Position;
import com.timekeeping.timekeeping.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/position")
public class PositionController {

    @Autowired
    private PositionService positionService;

    // Get all positions
    @GetMapping
    public String getAllPositions(Model model) {
        List<Position> positions = positionService.findAll();
        model.addAttribute("positions", positions);
        return "position/index"; // Assuming you have a Thymeleaf template named index.html under the "position" directory
    }

    // Get position by ID
    @GetMapping("/{id}")
    public String getPositionById(@PathVariable int id, Model model) {
        Optional<Position> position = Optional.ofNullable(positionService.findById(id));
        if (position.isPresent()) {
            model.addAttribute("position", position.get());
            return "position/detail"; // Assuming you have a Thymeleaf template named detail.html under the "position" directory
        } else {
            return "redirect:/position"; // Redirect if the position is not found
        }
    }

    // Create a new position (GET)
    @GetMapping("/create")
    public String createPositionForm(Model model) {
        model.addAttribute("position", new Position());
        return "position/create"; // Assuming you have a Thymeleaf template named create.html under the "position" directory
    }

    // Create a new position (POST)
    @PostMapping("/create")
    public String createPosition(@ModelAttribute Position position) {
        positionService.save(position);
        return "redirect:/position";
    }

    // Edit position (GET)
    @GetMapping("/edit/{id}")
    public String editPositionForm(@PathVariable int id, Model model) {
        Optional<Position> position = Optional.ofNullable(positionService.findById(id));
        if (position.isPresent()) {
            model.addAttribute("position", position.get());
            return "position/edit"; // Assuming you have a Thymeleaf template named edit.html under the "position" directory
        } else {
            return "redirect:/position";
        }
    }

    // Edit position (POST)
    @PostMapping("/edit")
    public String editPosition(@ModelAttribute Position position) {
        positionService.save(position);
        return "redirect:/position";
    }

    // Delete position
    @GetMapping("/delete/{id}")
    public String deletePosition(@PathVariable int id) {
        positionService.delete(id);
        return "redirect:/position";
    }

}
