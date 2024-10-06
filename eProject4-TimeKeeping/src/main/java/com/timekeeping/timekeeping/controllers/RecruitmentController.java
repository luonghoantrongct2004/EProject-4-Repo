package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Job;
import com.timekeeping.timekeeping.models.Notification;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.JobRepository;
import com.timekeeping.timekeeping.services.AccountService;
import com.timekeeping.timekeeping.services.JobService;
import com.timekeeping.timekeeping.services.RecruitmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recruitments")
public class RecruitmentController {
    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private AccountService accountService;

    @Autowired
    public RecruitmentController(RecruitmentService recruitmentService) {
        this.recruitmentService = recruitmentService;
    }

    @GetMapping("/list")
    public String listRecruitments(Model model) {
        List<Recruitment> recruitments = recruitmentService.findAll();
        model.addAttribute("recruitments", recruitments);
        return "recruitments/recruitment-list";
    }

    @GetMapping("/new")
    public String showRecruitmentForm(Model model) {
        model.addAttribute("recruitment", new Recruitment());
        model.addAttribute("jobs", jobService.findAll()); // assuming you have a jobService
        model.addAttribute("accounts", accountService.findAll()); // assuming you have an accountService
        return "recruitments/recruitment-schedule";
    }


//    @PostMapping
//    public String createRecruitment(@Validated @ModelAttribute Recruitment recruitment) {
//        recruitmentService.save(recruitment);
//        return "redirect:/recruitments/list";
//    }


//    @PostMapping("/schedule")
//    public String createRecruitment(@Validated @ModelAttribute Recruitment recruitment, Model model) {
//        recruitmentService.save(recruitment);
//        return "redirect:/recruitments/list";
//    }

//    @PostMapping("/schedule")
//    public String scheduleRecruitment(@Valid @ModelAttribute Recruitment recruitment,
//                                      BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            // If validation errors occur, re-populate the job and account dropdown lists
//            List<Job> jobs = jobService.findAll();
//            List<Account> accounts = accountService.findAll();
//            model.addAttribute("jobs", jobs);
//            model.addAttribute("accounts", accounts);
//            return "recruitments/recruitment-schedule";  // Return to the form on validation errors
//        }
//
//        // Retrieve Job and Account from the form submission
//        Job job = recruitment.getJob();
//        Account account = recruitment.getAccount();
//
//        // Ensure both Job and Account are selected
//        if (job == null || account == null) {
//            model.addAttribute("error", "Invalid Job or Account selected.");
//            List<Job> jobs = jobService.findAll();
//            List<Account> accounts = accountService.findAll();
//            model.addAttribute("jobs", jobs);
//            model.addAttribute("accounts", accounts);
//            return "recruitments/recruitment-schedule";  // Return to the form with the error message
//        }
//
//        // Check if the Job and Account combination is unique
//        if (!recruitmentService.isJobAndAccountUnique(job.getJobID(), account.getAccountID())) {
//            model.addAttribute("error", "This job and account combination is already scheduled.");
//            List<Job> jobs = jobService.findAll();
//            List<Account> accounts = accountService.findAll();
//            model.addAttribute("jobs", jobs);
//            model.addAttribute("accounts", accounts);
//            return "recruitments/recruitment-schedule";  // Return to the form with the error message
//        }
//
//        try {
//            // Save the recruitment if everything is valid
//            recruitmentService.saveRecruitment(recruitment);
//        } catch (Exception e) {
//            // Handle any exceptions that occur during saving
//            model.addAttribute("error", "Error scheduling recruitment: " + e.getMessage());
//            List<Job> jobs = jobService.findAll();
//            List<Account> accounts = accountService.findAll();
//            model.addAttribute("jobs", jobs);
//            model.addAttribute("accounts", accounts);
//            return "recruitments/recruitment-schedule";  // Return to the form with the error message
//        }
//
//        // Redirect to the recruitment list page after successful scheduling
//        return "redirect:/recruitments/list";
//    }


    // For creating recruitment with additional parameters via JSON
//    @PostMapping("/schedule")
//    public ResponseEntity<Recruitment> createRecruitmentWithDetails(
//            @RequestBody Recruitment recruitment,
//            @RequestParam int jobId,
//            @RequestParam int accountId) {
//        Recruitment createdRecruitment = recruitmentService.createRecruitment(recruitment, jobId, accountId);
//        return ResponseEntity.ok(createdRecruitment);
//    }


//    @PostMapping
//    public ResponseEntity<Recruitment> createRecruitment(@RequestBody Recruitment recruitment,
//                                                         @RequestParam int jobId,
//                                                         @RequestParam int accountId) {
//        Recruitment savedRecruitment = recruitmentService.createRecruitment(recruitment, jobId, accountId);
//        return ResponseEntity.ok(savedRecruitment);
//    }

//    @PostMapping("/schedule")
//    public String submitRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
//        try {
//            // Fetch the account based on the ID submitted from the form
//            Optional<Account> accountOptional = accountRepository.findById(recruitment.getAccount().getAccountID());
//
//            // Fetch the job based on the ID submitted from the form
//            Optional<Job> jobOptional = jobRepository.findById(recruitment.getJob().getJobID());
//
//            // Check if both account and job exist
//            if (accountOptional.isPresent() && jobOptional.isPresent()) {
//                recruitment.setAccount(accountOptional.get()); // Set the valid account
//                recruitment.setJob(jobOptional.get()); // Set the valid job
//                recruitmentService.save(recruitment);
//                model.addAttribute("successMessage", "Recruitment created successfully!");
//                return "redirect:/recruitments/list"; // Redirect to success page
//            } else {
//                String errorMessage = "";
//                if (!accountOptional.isPresent()) {
//                    errorMessage += "Account ID not found. ";
//                }
//                if (!jobOptional.isPresent()) {
//                    errorMessage += "Job ID not found.";
//                }
//                model.addAttribute("errorMessage", errorMessage);
//                return "recruitments/recruitment-schedule"; // Stay on the form page if there is an error
//            }
//        } catch (IllegalArgumentException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "recruitments/recruitment-schedule"; // Stay on the form page if there is an error
//        }
//    }

//    @PostMapping("/schedule")
//    public String scheduleRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
//        System.out.println("Received recruitment: " + recruitment);
//
//        // Check if accountID is already associated with a recruitment
//        if (recruitmentService.existsByAccountID(recruitment.getAccountID())) {
//            model.addAttribute("error", "Recruitment for this account ID already exists.");
//            System.out.println("Error: Recruitment for this account ID already exists.");
//            return "recruitments/recruitment-schedule"; // Replace with your actual form view name
//        }
//
//        // Save the recruitment information to the database
//        recruitmentService.save(recruitment);
//
//        // Redirect to the recruitment list page to display the updated information
//        return "redirect:/recruitments/list";
//    }


//    @PostMapping("/schedule")
//    public String scheduleRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
//        // Kiểm tra xem accountID đã có tuyển dụng chưa
//        if (recruitmentService.existsByAccountID(recruitment.getAccountID())) {
//            model.addAttribute("error", "Recruitment for this account ID already exists.");
//            return "recruitments/recruitment-schedule"; // Trả về form với thông báo lỗi
//        }
//
//        // Lưu thông tin tuyển dụng vào cơ sở dữ liệu
//        recruitmentService.save(recruitment);
//
//        // Chuyển hướng tới trang danh sách tuyển dụng để hiển thị thông tin đã cập nhật
//        return "redirect:/recruitments/list";
//    }

//    @GetMapping("/edit/{recruitmentID}")
//    public String getRecruitment(@PathVariable int recruitmentID, Model model) {
//        Optional<Recruitment> recruitment = recruitmentService.findById(recruitmentID);
//        if (recruitment.isPresent()) {
//            model.addAttribute("recruitment", recruitment.get());
//            return "recruitments/recruitment-edit";
//        } else {
//            model.addAttribute("error", "Recruitment not found");
//            return "recruitments/recruitment-list";
//        }
//    }


    @PostMapping("/schedule")
    public String scheduleRecruitment(@Valid @ModelAttribute Recruitment recruitment,
                                      BindingResult bindingResult, Model model) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            populateJobAndAccountLists(model);
            return "recruitments/recruitment-schedule";  // Return to the form on validation errors
        }

        // Retrieve Job and Account from the form submission
        Job job = recruitment.getJob();
        Account account = recruitment.getAccount();

        // Ensure both Job and Account are selected
        if (job == null || account == null) {
            model.addAttribute("error", "Invalid Job or Account selected.");
            populateJobAndAccountLists(model);
            return "recruitments/recruitment-schedule";  // Return to the form with the error message
        }

//        boolean exists = recruitmentService.existsByAccountIdAndJobId(account.getAccountID(), job.getJobID());
//        if (exists) {
//            model.addAttribute("error", "This account is already scheduled for this job.");
//            populateJobAndAccountLists(model); // Repopulate the dropdowns to maintain selection
//            return "recruitments/recruitment-schedule"; // Return to the scheduling page with error
//        }

        // Check if the Job and Account combination is unique
        if (!recruitmentService.isJobAndAccountUnique(job.getJobID(), account.getAccountID())) {
            bindingResult.rejectValue("job", "error.recruitment", "This job and account combination already exists.");
            model.addAttribute("jobs", jobService.findAll());
            model.addAttribute("accounts", accountService.findAll());
            return "recruitments/recruitment-schedule";  // Return to the form with the error message
        }

        try {
            // Save the recruitment if everything is valid
            recruitmentService.saveRecruitment(recruitment);
        } catch (Exception e) {
            // Handle any exceptions that occur during saving
            model.addAttribute("error", "Error scheduling recruitment: " + e.getMessage());
            populateJobAndAccountLists(model);
            return "recruitments/recruitment-schedule";  // Return to the form with the error message
        }

        // Redirect to the recruitment list page after successful scheduling
        return "redirect:/recruitments/list";
    }

    // Helper method to populate job and account lists
    private void populateJobAndAccountLists(Model model) {
        List<Job> jobs = jobService.findAll();
        List<Account> accounts = accountService.findAll();
        model.addAttribute("jobs", jobs);
        model.addAttribute("accounts", accounts);
    }



    @GetMapping("/edit/{recruitmentID}")
    public String getRecruitment(@PathVariable int recruitmentID, Model model) {
        Optional<Recruitment> recruitmentOptional = recruitmentService.findByID(recruitmentID); // Adjusted variable name for clarity
        if (recruitmentOptional.isPresent()) {
            Recruitment recruitment = recruitmentOptional.get();
            model.addAttribute("recruitment", recruitment);

            // Assuming you also need the lists of jobs and accounts for the dropdowns
            List<Job> jobs = jobService.findAll(); // Fetch all jobs
            List<Account> accounts = accountService.findAll(); // Fetch all accounts

            model.addAttribute("jobs", jobs);
            model.addAttribute("accounts", accounts);

            return "recruitments/recruitment-edit";
        } else {
            model.addAttribute("error", "Recruitment not found");
            return "redirect:/recruitments/list"; // Redirect if not found
        }
    }




//    @PostMapping("/update")
//    public String updateRecruitment(@ModelAttribute Recruitment recruitment, Model model) {
//        // Check if another recruitment entry has the same accountID
//        Recruitment existingRecruitment = recruitmentService.findByAccountID(recruitment.getAccountID());
//
//        if (existingRecruitment != null && existingRecruitment.getRecruitmentID() != recruitment.getRecruitmentID()) {
//            // A different recruitment entry with the same accountID exists
//            model.addAttribute("error", "Another recruitment with this account ID already exists.");
//            return "recruitments/recruitment-edit"; // Ensure this file exists in src/main/resources/templates/
//        }
//
//        // Save the updated recruitment information
//        recruitmentService.save(recruitment);
//
//        // Redirect to the recruitment list page after updating
//        return "redirect:/recruitments/list";
//    }

    @PostMapping("/update")
    public String updateRecruitment(@Valid @ModelAttribute Recruitment recruitment, BindingResult result, Model model) {
        // Validate input
        if (result.hasErrors()) {
            model.addAttribute("jobs", jobService.findAll());
            model.addAttribute("accounts", accountService.findAll());
            return "recruitments/recruitment-edit"; // Return to the form with errors
        }

        // Check if another recruitment with the same account exists
        Recruitment existingRecruitment = recruitmentService.findByAccount(recruitment.getAccount());
        if (existingRecruitment != null && existingRecruitment.getRecruitmentID() != recruitment.getRecruitmentID()) {
            model.addAttribute("error", "Another recruitment with this account ID already exists.");
            model.addAttribute("jobs", jobService.findAll());
            model.addAttribute("accounts", accountService.findAll());
            return "recruitments/recruitment-edit"; // Return to the form with an error
        }

        // Save the updated recruitment
        recruitmentService.save(recruitment);
        return "redirect:/recruitments/list"; // Redirect to the list after updating
    }

    @GetMapping("/delete/{recruitmentID}")
    public String deleteRecruitment(@PathVariable int recruitmentID) {
        recruitmentService.deleteById(recruitmentID);
        return "redirect:/recruitments/list";
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "test"; // Ensure there is a "test.html" template
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public String handleException(Exception e, Model model) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
        }
    }


}