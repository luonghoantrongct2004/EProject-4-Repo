package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.*;
import com.timekeeping.timekeeping.services.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PayrollService payrollService;
    @Autowired
    private SalaryTemplateService salaryTemplateService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Department.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Optional<Department> department = Optional.ofNullable(departmentService.findById(Integer.parseInt(text)));
                setValue(department.orElse(null));
            }
        });

        binder.registerCustomEditor(Position.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Optional<Position> position = Optional.ofNullable(positionService.findById(Integer.parseInt(text)));
                setValue(position.orElse(null));
            }
        });

        binder.registerCustomEditor(Role.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Optional<Role> role = Optional.ofNullable(roleService.findById(Integer.parseInt(text)));
                setValue(role.orElse(null));
            }
        });
    }
    @PostMapping("/export")
    @ResponseBody
    public void exportToExcel(@RequestParam("selectedAccounts") List<Integer> selectedAccountIds, HttpServletResponse response) throws IOException {
        // Fetch the selected accounts
        List<Account> selectedAccounts = accountService.findAllById(selectedAccountIds);

        // Set response header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=payroll_accounts.xlsx");

        // Create a workbook and a sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Payroll Accounts");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Full Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Gender");
        headerRow.createCell(4).setCellValue("Position");
        headerRow.createCell(5).setCellValue("Department");
        headerRow.createCell(6).setCellValue("Role");
        headerRow.createCell(7).setCellValue("Status");
        headerRow.createCell(8).setCellValue("Payment Date");
        headerRow.createCell(9).setCellValue("Gross Salary");
        headerRow.createCell(10).setCellValue("Net Salary");
        headerRow.createCell(11).setCellValue("Deductions");
        headerRow.createCell(12).setCellValue("Bonuses");


        // Fill the Excel sheet with account and payroll data
        int rowCount = 1;
        for (Account account : selectedAccounts) {
            List<Payroll> payrolls = payrollService.findPayrollsByAccount(account);  // Fetch payrolls for the account

            // If the account has payroll data, iterate through it
            for (Payroll payroll : payrolls) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(account.getAccountID());
                row.createCell(1).setCellValue(account.getFullName());
                row.createCell(2).setCellValue(account.getEmail());
                row.createCell(3).setCellValue(account.getGender());
                row.createCell(4).setCellValue(account.getPosition() != null ? account.getPosition().getName() : "N/A");
                row.createCell(5).setCellValue(account.getDepartment() != null ? account.getDepartment().getName() : "N/A");
                row.createCell(6).setCellValue(account.getRole() != null ? account.getRole().getName() : "N/A");
                row.createCell(7).setCellValue(account.getStatus());
                row.createCell(8).setCellValue(payroll.getPayDate().toString());
                row.createCell(9).setCellValue(payroll.getGrossSalary());
                row.createCell(10).setCellValue(payroll.getNetSalary());

                // Collect deductions and bonuses as strings
                String deductionsStr = payroll.getDeductions().stream()
                        .map(d -> d.getDescription() + ": " + d.getAmount())
                        .collect(Collectors.joining(", "));
                row.createCell(11).setCellValue(deductionsStr.isEmpty() ? "N/A" : deductionsStr);

                String bonusesStr = payroll.getBonuses().stream()
                        .map(b -> b.getBonusType() + ": " + b.getAmount())
                        .collect(Collectors.joining(", "));
                row.createCell(12).setCellValue(bonusesStr.isEmpty() ? "N/A" : bonusesStr);
            }
        }

        // Write the Excel file to the response output stream
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping
    public String getAllEmployee(@RequestParam(value = "name", required = false) String name, Model model) {
        List<Account> accounts;

        if (name != null && !name.isEmpty()) {
            accounts = accountService.findByName(name); // Assuming you're searching by employee name or account name
        } else {
            accounts = accountService.findAll();
        }

        model.addAttribute("accounts", accounts);
        return "employees/index"; // Assuming you have a Thymeleaf template named index.html under the "employees" directory
    }


    @GetMapping("/{id}")
    public String getEmployeeById(@PathVariable int id, Model model) {
        Optional<Account> account = accountService.findById(id);
        if (account.isPresent()) {
            model.addAttribute("account", account.get());
            return "employees/detail"; // This should be the correct Thymeleaf view
        } else {
            return "redirect:/employee"; // Redirect if account not found
        }
    }


    @GetMapping("/create")
    public String createEmployee(Model model) {
        model.addAttribute("account", new Account());
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        List<Position> positions = positionService.findAll();
        model.addAttribute("positions", positions);
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        List<SalaryTemplate> salaryTemplates = salaryTemplateService.findAllSalaryTemplates();
        model.addAttribute("salaries", salaryTemplates);

        return "employees/create";
    }

    @PostMapping("/create")
    public String createEmployee(@ModelAttribute Account account,
                                 @RequestParam("salaryTemplate") SalaryTemplate salaryTemplate,
                                 @RequestParam("images") MultipartFile[] images,
                                 RedirectAttributes redirectAttributes) {
        account.setSalaryTemplate(salaryTemplate);
        // Lưu thông tin tài khoản lần đầu tiên để lấy account_id
        accountService.save(account, account.getRole());
        String processedFullName = removeAccentAndSpaces(account.getFullName());
        List<String> imagePaths = new ArrayList<>();

        // Kiểm tra số lượng ảnh không vượt quá 5
        if (images.length > 5) {
            redirectAttributes.addFlashAttribute("error", "Upload maximun 5 image!!");
            return "redirect:/employee/create";
        }

        // Tạo thư mục lưu ảnh
        String uploadDir = "src/main/resources/static/Data/" + processedFullName;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Lưu từng ảnh và đường dẫn của chúng
        for (int i = 0; i < images.length; i++) {
            MultipartFile file = images[i];
            String fileName = (i + 1) + ".jpeg";

            try {
                // Lưu ảnh vào thư mục
                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, file.getBytes());

                // Lưu đường dẫn tương đối vào danh sách imagePaths
                String relativePath = "/Data/" + processedFullName + "/" + fileName;
                imagePaths.add(relativePath);

            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error upload image!!");
                return "redirect:/employee/create";
            }
        }

        // Chuyển danh sách đường dẫn ảnh thành JSON và lưu vào account
        account.setImagePaths(imagePaths);

        // Cập nhật lại account sau khi đã có imagePaths
        accountService.update(account);

        // Thông báo thành công
        redirectAttributes.addFlashAttribute("success", "Create employee successfully!");
        return "redirect:/employee";
    }




    public static String removeAccentAndSpaces(String fullName) {
        // Loại bỏ dấu cách
        String noSpaces = fullName.replaceAll("\\s+", "");

        // Loại bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(noSpaces, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable int id, Model model) {
        Optional<Account> account = accountService.findById(id);
        if (account.isPresent()) {
            model.addAttribute("account", account.get());
            List<Role> roles = roleService.findByActive();
            model.addAttribute("roles", roles);
            List<Position> positions = positionService.findAll();
            model.addAttribute("positions", positions);
            List<Department> departments = departmentService.findAll();
            model.addAttribute("departments", departments);
            return "employees/edit";
        } else {
            return "redirect:/employee";
        }
    }
    @PostMapping("/edit")
    public String editEmployee(@ModelAttribute Account account,
                               @RequestParam(value = "newImages", required = false) MultipartFile[] newImages,
                               RedirectAttributes redirectAttributes) {
        // Fetch the current account from the database
        Optional<Account> currentAccountOpt = accountService.findById(account.getAccountID());
        if (currentAccountOpt.isPresent()) {
            Account currentAccount = currentAccountOpt.get();

            // Preserve the old password if the new one isn't provided
            if (account.getPassword() == null || account.getPassword().isEmpty()) {
                account.setPassword(currentAccount.getPassword());
            } else {
                // Encode the new password
                account.setPassword(passwordEncoder.encode(account.getPassword()));
            }

            // Keep the existing image paths
            List<String> imagePaths = currentAccount.getImagePaths() != null ? currentAccount.getImagePaths() : new ArrayList<>();
            String processedFullName = removeAccentAndSpaces(account.getFullName());

            // Create upload directory if it doesn't exist
            String uploadDir = "src/main/resources/static/Data/" + processedFullName;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Count existing files in the directory
            File[] existingFiles = directory.listFiles((dir, name) -> name.endsWith(".jpeg"));
            int currentFileCount = existingFiles != null ? existingFiles.length : 0;

            // Check if new images exceed the limit of 5
            if (newImages != null && newImages.length > 0) {
                if (currentFileCount + newImages.length > 5) {
                    redirectAttributes.addFlashAttribute("error", "You can only save a maximum of 5 images!");
                    return "redirect:/employee/edit/" + account.getAccountID();
                }

                // Save each new image
                for (int i = 0; i < newImages.length; i++) {
                    MultipartFile file = newImages[i];
                    String fileName = (currentFileCount + i + 1) + ".jpeg";  // Naming files from 1 to 5

                    try {
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.write(filePath, file.getBytes());

                        String relativePath = "/Data/" + processedFullName + "/" + fileName;
                        imagePaths.add(relativePath); // Add the new image path to the existing list

                    } catch (IOException e) {
                        redirectAttributes.addFlashAttribute("error", "Error saving the image!");
                        return "redirect:/employee/edit/" + account.getAccountID();
                    }
                }
            }

            // Update the account with the new list of images
            account.setImagePaths(imagePaths);

            // Save changes to the account
            accountService.update(account);

            redirectAttributes.addFlashAttribute("success", "Account updated successfully!");
            return "redirect:/employee";
        } else {
            redirectAttributes.addFlashAttribute("error", "Account does not exist!");
            return "redirect:/employee";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable int id, RedirectAttributes redirectAttributes) {
        accountService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Successfully deactivated.");
        return "redirect:/employee";
    }

    @GetMapping("/activate/{id}")
    public String activateEmployee(@PathVariable int id, RedirectAttributes redirectAttributes) {
        accountService.turnOn(id);
        redirectAttributes.addFlashAttribute("success", "Successfully activated.");
        return "redirect:/employee";
    }


}

