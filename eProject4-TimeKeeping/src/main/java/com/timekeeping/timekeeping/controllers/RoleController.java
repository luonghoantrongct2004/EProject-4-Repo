package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Role;
import com.timekeeping.timekeeping.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public String getAllRoles(@RequestParam(value = "name", required = false) String name, Model model) {
        List<Role> roles;

        if (name != null && !name.isEmpty()) {
            roles = roleService.findByName(name);
        } else {
            roles = roleService.findAll();
        }

        model.addAttribute("roles", roles);
        return "roles/index"; // Assuming you have a Thymeleaf template named index.html under the "roles" directory
    }


    @GetMapping("/{id}")
    public String getRoleById(@PathVariable int id, Model model) {
        Optional<Role> role = Optional.ofNullable(roleService.findById(id));
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "roles/detail"; // Assuming you have a Thymeleaf template named detail.html under the "roles" directory
        } else {
            return "redirect:/role"; // Redirect if the role is not found
        }
    }

    @GetMapping("/create")
    public String createRole(Model model) {
        model.addAttribute("role", new Role());
        return "roles/create"; // Assuming you have a Thymeleaf template named create.html under the "roles" directory
    }

    @PostMapping("/create")
    public String createRole(@ModelAttribute Role role) {
        roleService.save(role);
        return "redirect:/role";
    }

    @GetMapping("/edit/{id}")
    public String editRole(@PathVariable int id, Model model) {
        Optional<Role> role = Optional.ofNullable(roleService.findById(id));
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "roles/edit";
        } else {
            return "redirect:/role";
        }
    }

    @PostMapping("/edit")
    public String editRole(@ModelAttribute Role role) {
        roleService.save(role);
        return "redirect:/role";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable int id) {
        roleService.delete(id);
        return "redirect:/role";
    }

    @GetMapping("/activate/{id}")
    public String activateRole(@PathVariable int id) {
        roleService.activate(id);
        return "redirect:/role";
    }
}
