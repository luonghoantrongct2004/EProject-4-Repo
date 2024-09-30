package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Payroll;
import com.timekeeping.timekeeping.models.Role;
import com.timekeeping.timekeeping.services.AccountService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    public AuthController(AuthenticationManager authenticationManager, EntityManager entityManager) {
        this.authenticationManager = authenticationManager;
        this.entityManager = entityManager;
    }
    @Autowired
    private AccountService accountService;

    private EntityManager entityManager;
    @GetMapping("/payrollEmployee")
    public String payrollEmployee(@RequestParam("accountID") int accountID, Model model) {
        List<Payroll> payrolls = entityManager.createQuery(
                        "SELECT p FROM Payroll p WHERE p.account.accountID = :accountID", Payroll.class)
                .setParameter("accountID", accountID)
                .getResultList();
        Optional<Account> accountOptional = accountService.findById(accountID);

            Account account = accountOptional.get();
            model.addAttribute("account", account);
        model.addAttribute("payrolls", payrolls);

        return "auth/payrollView";
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("account", new Account());
        return "auth/login";
    }

    @PostMapping("/loginSubmit")
    public String login(@ModelAttribute Account account, Model model, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            if (account.getEmail() == null || account.getEmail().isEmpty() ||
                    account.getPassword() == null || account.getPassword().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username and password cannot be empty!");
                return "redirect:/auth/login";
            }

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                Optional<Account> authenticatedAccountOptional = accountService.findByUsername(account.getEmail());

                if (authenticatedAccountOptional.isPresent()) {
                    Account authenticatedAccount = authenticatedAccountOptional.get();

                    // Check if the account is active
                    if (authenticatedAccount.getStatus() == "InActive") {
                        redirectAttributes.addFlashAttribute("error", "Your account is not active!");
                        return "redirect:/auth/login";
                    }

                    Role role = authenticatedAccount.getRole();

                    // Create AUTH-TOKEN cookie
                    Cookie authCookie = new Cookie("AUTH-TOKEN", authentication.getName());
                    authCookie.setHttpOnly(false);
                    authCookie.setSecure(false);
                    authCookie.setMaxAge(7 * 24 * 60 * 60);
                    authCookie.setPath("/");

                    Cookie accountIdCookie = new Cookie("ACCOUNT-ID", String.valueOf(authenticatedAccount.getAccountID()));
                    accountIdCookie.setHttpOnly(false);
                    accountIdCookie.setSecure(false);
                    accountIdCookie.setMaxAge(7 * 24 * 60 * 60);
                    accountIdCookie.setPath("/");

                    Cookie roleIdCookie = new Cookie("ROLE-ID", String.valueOf(role.getRoleID()));
                    roleIdCookie.setHttpOnly(false);
                    roleIdCookie.setSecure(false);
                    roleIdCookie.setMaxAge(7 * 24 * 60 * 60);
                    roleIdCookie.setPath("/");

                    // Add cookies to response
                    response.addCookie(authCookie);
                    response.addCookie(accountIdCookie);
                    response.addCookie(roleIdCookie);

                    // Check role ID and redirect accordingly
                    if (role.getRoleID() >= 1 && role.getRoleID() <= 3) {
                        redirectAttributes.addFlashAttribute("success", "Welcome " + role.getName() + " to the admin panel!");
                        return "redirect:/dashboard";
                    } else {
                        redirectAttributes.addFlashAttribute("success", "Welcome " + account.getFullName() + " to TimeKeeping!");
                        return "redirect:/home";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("error", "User not found!");
                    return "redirect:/auth/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Incorrect username or password!");
                return "redirect:/auth/login";
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Incorrect email or password!");
            return "redirect:/auth/login";
        }
    }


    @GetMapping("/profile")
    public String profile(Model model, @RequestParam("AccountId") int accountId) {
        Optional<Account> accountOptional = accountService.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            model.addAttribute("account", account);
            return "auth/profile";
        } else {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(@RequestParam("accountID") int accountId,
                                       @RequestParam("currentPassword") String currentPassword,
                                       @RequestParam("newPassword") String newPassword,
                                       Model model, RedirectAttributes redirectAttributes) {
        Optional<Account> foundAccountOptional = accountService.findById(accountId);

        if (foundAccountOptional.isPresent()) {
            Account foundAccount = foundAccountOptional.get();

            // Check if current password is correct
            if (accountService.checkPassword(currentPassword, foundAccount.getPassword())) {
                foundAccount.setPassword(newPassword);
                accountService.save(foundAccount, foundAccount.getRole());
                redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
                return "redirect:/auth/profile?AccountId=" + accountId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
                return "redirect:/auth/profile?AccountId=" + accountId;
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Account does not exist!");
            return "redirect:/auth/profile?AccountId=" + accountId;
        }
    }



}
