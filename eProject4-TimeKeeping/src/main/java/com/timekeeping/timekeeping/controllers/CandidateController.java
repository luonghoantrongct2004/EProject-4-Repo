package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Candidate;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.CandidateRepository;
import com.timekeeping.timekeeping.services.CandidateService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Value("${file.upload-dir}")
    private String uploadDir;
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";


    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("candidateID") int candidateID,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            candidateService.updateStatus(candidateID, status);
            redirectAttributes.addFlashAttribute("message", "Status updated successfully!");
        } catch (Exception e) {
            logger.error("Error updating candidate status", e);
            redirectAttributes.addFlashAttribute("error", "Failed to update status.");
        }
        return "redirect:/candidates/list";
    }

    @GetMapping("/list")
    public String listCandidates(Model model) {
        List<Candidate> candidates = candidateService.findAll();
        model.addAttribute("candidates", candidates);
        return "apply/candidate-list";
    }

    @GetMapping("/apply")
    public String showCandidateForm(Model model) {
        model.addAttribute("candidate", new Candidate());
        return "apply/apply-form";
    }

    @GetMapping("/{candidateID}")
    public String viewCandidateDetails(@PathVariable int candidateID, Model model) {
        Candidate candidate = candidateService.findById(candidateID).orElseThrow(() -> new IllegalArgumentException("Invalid candidate Id:" + candidateID));
        model.addAttribute("candidate", candidate);
        return "apply/candidate-details"; // Thymeleaf template name for details view
    }

    @PostMapping("/apply")
    public String apply(@ModelAttribute @Valid Candidate candidate,
                        BindingResult bindingResult,
                        @RequestParam("resume") MultipartFile resume,
                        @RequestParam("profilePicture") MultipartFile profilePicture,
                        RedirectAttributes redirectAttributes) {

        logger.debug("Received candidate: " + candidate.toString());
        logger.debug("Resume file: " + resume.getOriginalFilename());
        logger.debug("Profile picture file: " + profilePicture.getOriginalFilename());

        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: ");
            bindingResult.getAllErrors().forEach(error -> {
                logger.error("Object Name: " + error.getObjectName());
                logger.error("Default Message: " + error.getDefaultMessage());
            });
            return "apply/apply-form";
        }
        try {
            // Save files if present
            String resumePath = resume.isEmpty() ? null : saveFile(resume);
            String profilePicturePath = profilePicture.isEmpty() ? null : saveFile(profilePicture);

            // Set file paths in candidate object
            candidate.setResume(resumePath);
            candidate.setProfilePicturePath(profilePicturePath);

            // Save candidate information to the database
            candidateService.save(candidate);
            logger.info("Candidate saved successfully");

            // Redirect to success page
            redirectAttributes.addFlashAttribute("message", "Application submitted successfully!");
            return "redirect:/candidates/apply-success";

        } catch (IOException e) {
            logger.error("File upload error", e);
            redirectAttributes.addFlashAttribute("error", "File upload failed!");
            return "redirect:/candidates/apply";
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Construct the file name and destination path
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path destinationFile = Paths.get(uploadDir).resolve(fileName);

        // Save the file
        try {
            file.transferTo(destinationFile.toFile());
        } catch (IOException e) {
            logger.error("Error saving file: ", e);
            throw e;
        }

        logger.info("File saved at: " + destinationFile.toString());
        return "/images/" + fileName;
    }
//    @PostMapping("/candidates/updateStatus")
//    public String updateStatus(@RequestParam("candidateID") int candidateID, @RequestParam("status") String status) {
//        // Update the candidate status in the database
//        candidateService.updateStatus(candidateID, status);
//        return "redirect:/candidates/list";
//    }


//    @PostMapping("/apply")
//    public String submitApplication(
//            @ModelAttribute("candidate") @Valid Candidate candidate,
//            BindingResult bindingResult,
//            @RequestParam("resume") MultipartFile resume,
//            @RequestParam("profilePicture") MultipartFile profilePicture,
//            RedirectAttributes redirectAttributes) throws IOException {
//
//        if (bindingResult.hasErrors()) {
//            return "apply-form";
//        }
//
//        // Process file uploads
//        if (!resume.isEmpty()) {
//            String resumePath = candidateService.saveFile(resume);
//            candidate.setResume(resumePath);
//        }
//
//        if (!profilePicture.isEmpty()) {
//            String profilePicturePath = candidateService.saveFile(profilePicture);
//            candidate.setProfilePicturePath(profilePicturePath);
//        }
//
//        candidateService.save(candidate, resume, profilePicture);
//
//        redirectAttributes.addFlashAttribute("message", "Application submitted successfully!");
//        return "redirect:/apply-success";
//    }


    @GetMapping("/apply-success")
    public String showSuccessPage(Model model) {
        return "apply/apply-success";
    }

    @GetMapping("/edit/{candidateID}")
    public String editCandidate(@PathVariable int candidateID, Model model) {
        Candidate candidate = candidateService.findById(candidateID).orElseThrow(() -> new IllegalArgumentException("Invalid candidate Id:" + candidateID));
        model.addAttribute("candidate", candidate);
        return "apply/candidate-form";
    }

    @PostMapping("/update/{candidateID}")
    public String updateCandidate(@PathVariable int candidateID,
                                  @ModelAttribute Candidate candidate,
                                  @RequestParam("resume") MultipartFile resume,
                                  @RequestParam("profilePicture") MultipartFile profilePicture) throws IOException {

        // Update candidateID
        candidate.setCandidateID(candidateID);

        // Check if there are new files and save them
        if (!resume.isEmpty()) {
            String resumePath = saveFile(resume);
            candidate.setResume(resumePath);
        }

        if (!profilePicture.isEmpty()) {
            String profilePicturePath = saveFile(profilePicture);
            candidate.setProfilePicturePath(profilePicturePath);
        }

        // Save the updated candidate
        candidateService.save(candidate, resume, profilePicture);

        // Redirect after successful update
        return "redirect:/candidates/list";
    }



    @GetMapping("/delete/{candidateID}")
    public String deleteCandidate(@PathVariable int candidateID) {
        candidateService.deleteById(candidateID);
        return "redirect:/candidates/list";
    }


    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
            model.addAttribute("error", "Validation failed: " + ex.getMessage());
            return "error";
        }

        @ExceptionHandler(IOException.class)
        public String handleIOException(IOException ex, Model model) {
            model.addAttribute("error", "File handling error: " + ex.getMessage());
            return "error";
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {
            model.addAttribute("error", "File size exceeds limit: " + ex.getMessage());
            return "error";
        }

        @ExceptionHandler(Exception.class)
        public String handleException(Exception ex, Model model) {
            model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
            return "error";
        }
    }



        @PostConstruct
        public void init() {
            // Define the directory path from the upload directory property
            File directory = new File(uploadDir);

            // Create directory if it does not exist
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }



}
