package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Role;
import com.timekeeping.timekeeping.services.AccountService;
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

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("account", new Account());
        return "auth/login";
    }

    @PostMapping("/loginSubmit")
    public String login(@ModelAttribute Account account, Model model, HttpServletResponse response,RedirectAttributes redirectAttributes) {
        try {
            if (account.getEmail() == null || account.getEmail().isEmpty() ||
                    account.getPassword() == null || account.getPassword().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên đăng nhập và mật khẩu không được để trống!");
                return "redirect:/auth/login";
            }
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                Optional<Account> authenticatedAccountOptional = accountService.findByUsername(account.getEmail());

                if (authenticatedAccountOptional.isPresent()) {
                    Account authenticatedAccount = authenticatedAccountOptional.get();
                    Role role = authenticatedAccount.getRole();

                    // Tạo cookie AUTH-TOKEN
                    Cookie authCookie = new Cookie("AUTH-TOKEN", authentication.getName());
                    authCookie.setHttpOnly(false); // Đặt là false để JavaScript có thể truy cập
                    authCookie.setSecure(false);   // Đặt là false cho môi trường phát triển (HTTPS mới cần true)
                    authCookie.setMaxAge(7 * 24 * 60 * 60); // Đặt thời gian hết hạn
                    authCookie.setPath("/"); // Đảm bảo đường dẫn cookie phù hợp

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

                    response.addCookie(authCookie);
                    response.addCookie(accountIdCookie);
                    response.addCookie(roleIdCookie);


                    // Kiểm tra ID của vai trò và chuyển hướng tương ứng
                    if (role.getRoleID() >= 1 && role.getRoleID() <= 3) {
                        redirectAttributes.addFlashAttribute("success", "Xin chào" + role.getName() + "đến với trang quản trị!");
                        return "redirect:/dashboard";
                    } else {
                        redirectAttributes.addFlashAttribute("success", "Xin chào" + account.getFullName() + "đến với TimeKeeping!");
                        return "redirect:/home";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!!");
                    return "auth/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Sai tài khoản hoặc mật khẩu!!");
                return "redirect:/auth/login";
            }
        }catch (AuthenticationException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Sai email hoặc mật khẩu!!");
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
            return "auth/login";
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

            // Kiểm tra mật khẩu hiện tại có đúng không
            if (accountService.checkPassword(currentPassword, foundAccount.getPassword())) {
                foundAccount.setPassword(newPassword);
                accountService.save(foundAccount, foundAccount.getRole());
                redirectAttributes.addFlashAttribute("success", "Đã đổi mật khẩu thành công!!");
                return "redirect:/auth/profile?AccountId=" + accountId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!!");
                return "redirect:/auth/profile?AccountId=" + accountId;
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại!!");
            return "redirect:/auth/profile?AccountId=" + accountId;
        }
    }


}
