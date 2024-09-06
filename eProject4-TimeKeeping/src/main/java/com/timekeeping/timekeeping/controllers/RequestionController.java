package com.timekeeping.timekeeping.controllers;


import com.timekeeping.timekeeping.models.Requestion;
import com.timekeeping.timekeeping.services.RequestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/requestion")
public class RequestionController {
    @Autowired
    private RequestionService requestionService;

    @GetMapping
    public String getallRequestions(Model model) {
        model.addAttribute("requestions", requestionService.getAllRequestions());
        model.addAttribute("requestion", new Requestion());
        return "requestion/index";
    }





    @GetMapping("/{id}")
    public String getRequestionByID(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Requestion> requestion = requestionService.getRequestionById(id);
        if (requestion.isPresent()) {
            model.addAttribute("requestion", requestion.get());
            return "requestion/detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Yêu cầu không tồn tại.");
            return "redirect:/requestion";
        }
    }


    @PostMapping
    public String saveRequestion(@ModelAttribute("requestion") Requestion requestion) {
        requestionService.saveRequestion(requestion);
        return "redirect:/requestion";
    }

    @GetMapping("/find")
    public String findByRequestionID(@RequestParam("requestID") int requestID, Model model) {
        model.addAttribute("requestion", requestionService.getRequestionById(requestID));
        return "requestion/index";
    }

    @PostMapping("/confirm/{id}")
    public String confirmRequestion(@PathVariable("id") int id, Model model) {
        Optional<Requestion> requestionOptional = requestionService.getRequestionById(id);
        if (requestionOptional.isPresent()) {
            Requestion requestion = requestionOptional.get();

            if ("Đang chờ phê duyệt".equals(requestion.getStatus())) {
                requestion.setStatus("Đã phê duyệt");
                requestionService.updateRequestion(requestion);
                model.addAttribute("message", "Yêu cầu đã được phê duyệt.");
                return "requestion/index";
            } else {
                model.addAttribute("message", "Yêu cầu này không thể phê duyệt vì nó không ở trạng thái 'Đang chờ phê duyệt'.");
                return "requestion/index";
            }
        } else {
            model.addAttribute("message", "Yêu cầu không tồn tại.");
            return "requestion/index";
        }
    }

    @PostMapping("/deny/{id}")
    public String denyRequestion(@PathVariable("id") int id, Model model) {
        Optional<Requestion> requestionOptional = requestionService.getRequestionById(id);
        if (requestionOptional.isPresent()) {
            Requestion requestion = requestionOptional.get();

            if ("Đang chờ phê duyệt".equals(requestion.getStatus())) {
                requestion.setStatus("Đã từ chối");
                requestionService.updateRequestion(requestion);
                model.addAttribute("message", "Yêu cầu đã bị từ chối.");
                return "requestion/index";
            } else {
                model.addAttribute("message", "Yêu cầu này không thể tù chối vì nó không ở trạng thái 'Đang chờ phê duyệt'.");
                return "requestion/index";
            }
        } else {
            model.addAttribute("message", "Yêu cầu không tồn tại.");
            return "requestion/index";
        }
    }

    @GetMapping("/filter")
    public String filterByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            Model model) {
        List<Requestion> requestions = requestionService.filterByDateRange(startDate, endDate);
        model.addAttribute("requestions", requestions);
        return "requestion/index";
    }


}
