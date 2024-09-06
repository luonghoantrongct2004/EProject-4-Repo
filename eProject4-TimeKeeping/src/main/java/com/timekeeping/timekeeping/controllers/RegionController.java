package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Region;
import com.timekeeping.timekeeping.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/region")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public String getAllRegions(@RequestParam(value = "name", required = false) String name, Model model) {
        List<Region> regions;

        if (name != null && !name.isEmpty()) {
            regions = regionService.findByName(name);
        } else {
            regions = regionService.findAll();
        }

        model.addAttribute("regions", regions);
        return "region/index"; // Assuming you have a Thymeleaf template named index.html under the "region" directory
    }


    @GetMapping("/{id}")
    public String getRegionById(@PathVariable int id, Model model) {
        Optional<Region> region = Optional.ofNullable(regionService.findById(id));
        if (region.isPresent()) {
            model.addAttribute("region", region.get());
            return "region/detail";
        } else {
            return "redirect:/region";
        }
    }

    @GetMapping("/create")
    public String createRegion(Model model) {
        model.addAttribute("region", new Region());
        return "region/create"; // Assuming you have a Thymeleaf template named create.html under the "regions" directory
    }

    @PostMapping("/create")
    public String createRegion(@ModelAttribute Region region) {
        regionService.save(region);
        return "redirect:/region";
    }

    @GetMapping("/edit/{id}")
    public String editRegion(@PathVariable int id, Model model) {
        Optional<Region> region = Optional.ofNullable(regionService.findById(id));
        if (region.isPresent()) {
            model.addAttribute("region", region.get());
            return "region/edit";
        } else {
            return "redirect:/region";
        }
    }

    @PostMapping("/edit")
    public String editRegion(@ModelAttribute Region region) {
        regionService.save(region);
        return "redirect:/region";
    }

    @GetMapping("/delete/{id}")
    public String deleteRegion(@PathVariable int id) {
        regionService.delete(id);
        return "redirect:/region";
    }

}
