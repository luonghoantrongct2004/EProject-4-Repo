package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Department;
import com.timekeeping.timekeeping.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Get all departments
    @GetMapping
    public String getAllDepartments(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "department/index"; // Assuming you have a Thymeleaf template named index.html under the "department" directory
    }

    // Get department by ID
    @GetMapping("/{id}")
    public String getDepartmentById(@PathVariable int id, Model model) {
        Optional<Department> department = Optional.ofNullable(departmentService.findById(id));
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "department/detail"; // Assuming you have a Thymeleaf template named detail.html under the "department" directory
        } else {
            return "redirect:/department"; // Redirect if the department is not found
        }
    }

    // Create a new department (GET)
    @GetMapping("/create")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "department/create"; // Assuming you have a Thymeleaf template named create.html under the "department" directory
    }

    // Create a new department (POST)
    @PostMapping("/create")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.save(department);
        return "redirect:/department";
    }

    // Edit department (GET)
    @GetMapping("/edit/{id}")
    public String editDepartmentForm(@PathVariable int id, Model model) {
        Optional<Department> department = Optional.ofNullable(departmentService.findById(id));
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "department/edit"; // Assuming you have a Thymeleaf template named edit.html under the "department" directory
        } else {
            return "redirect:/department";
        }
    }

    // Edit department (POST)
    @PostMapping("/edit")
    public String editDepartment(@ModelAttribute Department department) {
        departmentService.save(department);
        return "redirect:/department";
    }

    // Delete department
    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable int id) {
        departmentService.delete(id);
        return "redirect:/department";
    }
}
