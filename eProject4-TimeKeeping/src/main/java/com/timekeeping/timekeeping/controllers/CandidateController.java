package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Candidate;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.CandidateRepository;
import com.timekeeping.timekeeping.services.CandidateService;
import com.timekeeping.timekeeping.services.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.xml.security.stax.ext.SecurePart.Modifier.Content;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @Autowired
    private EmailService emailService;

    @Value("${cloudinary.upload.preset:default_value}")
    private String uploadPreset;


    @Value("${sendgrid.from.email}") // You can configure this in application.properties
    private String fromEmail;
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }


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
    public String applyForPosition(@ModelAttribute @Valid Candidate candidate,
                                   BindingResult bindingResult,
                                   @RequestParam("resume") MultipartFile resume,
                                   @RequestParam("profilePicture") MultipartFile profilePicture,
                                   RedirectAttributes redirectAttributes) {

//        if (bindingResult.hasErrors()) {
//            logger.error("Validation errors: {}", bindingResult.getAllErrors());
//            return "apply/apply-form";
//        }

        try {
            // Handle file uploads and set file paths to candidate
            String resumePath = handleFileUpload(resume);
            String profilePicturePath = handleFileUpload(profilePicture);
            candidate.setResume(resumePath);
            candidate.setProfilePicturePath(profilePicturePath);

            // Save candidate to the database
            candidateService.save(candidate);

            String toEmail = candidate.getEmail(); // Assuming the Candidate has an email field
            String subject = "Application Submitted Successfully";
            String messageContent = "Dear " + candidate.getRequirements() + ",\n\n" +
                    "Thank you for applying for the position. We have received your application.\n\n" +
                    "Best regards,\n" +
                    "The Recruitment Team";

            // Send email notification
            emailService.sendEmailWithSendGrid(toEmail, subject, messageContent, fromEmail);


            redirectAttributes.addFlashAttribute("message", "Application submitted successfully!");
            return "redirect:/candidates/apply-success";

        } catch (IOException e) {
            logger.error("Error uploading files", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload files. Please try again.");
            return "redirect:/candidates/apply";
        }
    }

    private void sendApplicationEmail(Candidate candidate) {
        // Get the candidate's email address
        String toEmail = candidate.getEmail(); // Ensure Candidate has an email field

        // Define the subject of the email
        String subject = "Application Submitted Successfully";

        // Construct the email content with more personalization
        String messageContent = String.format(
                "Dear %s,\n\n" +
                        "Thank you for your application for the position of %s.\n\n" +
                        "We appreciate your interest in joining our team and will review your application thoroughly. " +
                        "If your qualifications match our needs, we will reach out to you soon.\n\n" +
                        "Best regards,\n" +
                        "The Recruitment Team\n" +
                        "[Your Company Name]\n\n" +
                        "If you have any questions, feel free to reply to this email.",
                candidate.getEmail(), // Assuming Candidate has a method to get full name
                candidate.getRequirements()   // Assuming Candidate has a method to get the applied position
        );

        // Send the email using the email service
        emailService.sendEmailWithSendGrid(toEmail, subject, messageContent, fromEmail);
    }



    public String handleFileUpload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        // Define the directory where the file will be saved
        String uploadDir = "src/main/resources/static/uploads/";

        // Ensure the directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();  // Create the directory if it doesn't exist
        }

        // Generate a unique file name using UUID and preserve the file extension
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";

        // Extract the file extension if it exists
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Create a unique file name using UUID
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Save the file to the specified location
        String filePath = uploadDir + uniqueFileName;
        Path path = Paths.get(filePath);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path to the file
        return "/uploads/" + uniqueFileName;
    }



//    @PostMapping("/apply")
//    public String applyForPosition(@ModelAttribute Candidate candidate,
//                                   @RequestParam("resume") MultipartFile resume,
//                                   @RequestParam("profilePicture") MultipartFile profilePicture,
//                                   Model model) {
//        try {
//            // Convert file content to base64
//            String resumeBase64 = convertFileToBase64(resume);
//            candidate.setResume(resumeBase64);
//
//            String profilePictureBase64 = convertFileToBase64(profilePicture);
//            candidate.setProfilePicture(profilePictureBase64);
//
//            // Save candidate information to the database
//            candidateService.save(candidate);
//
//            return "redirect:/candidates/apply-success";
//        } catch (IOException e) {
//            model.addAttribute("error", "File upload failed: " + e.getMessage());
//            return "apply/apply-form";
//        }
//    }
//
//    private String convertFileToBase64(MultipartFile file) throws IOException {
//        if (file.isEmpty()) {
//            return null;
//        }
//
//        byte[] fileBytes = file.getBytes();
//        return Base64.getEncoder().encodeToString(fileBytes);
//    }
//
//
//
//    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
//        // Ensure the directory exists
//        File dir = new File(uploadDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        // Generate a unique file name and save the file
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        File saveFile = new File(dir, fileName);
//        file.transferTo(saveFile);
//
//        return "/static/" + fileName;
//    }
//


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
        Candidate candidate = candidateService.findById(candidateID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid candidate Id:" + candidateID));
        model.addAttribute("candidate", candidate);
        return "apply/candidate-form"; // Return to candidate edit form
    }

//    @PostMapping("/update/{candidateID}")
//    public String updateCandidate(@PathVariable int candidateID,
//                                  @ModelAttribute Candidate candidate,
//                                  @RequestParam("resume") MultipartFile resume,
//                                  @RequestParam("profilePicture") MultipartFile profilePicture) throws IOException {
//
//        // Update candidateID
//        candidate.setCandidateID(candidateID);
//
//        // Check if there are new files and save them
//        if (!resume.isEmpty()) {
//            String resumePath = saveFile(resume);
//            candidate.setResume(resumePath);
//        }
//
//        if (!profilePicture.isEmpty()) {
//            String profilePicturePath = saveFile(profilePicture);
//            candidate.setProfilePicturePath(profilePicturePath);
//        }
//
//        // Save the updated candidate
//        candidateService.save(candidate, resume, profilePicture);
//
//        // Redirect after successful update
//        return "redirect:/candidates/list";
//    }
//


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




}
