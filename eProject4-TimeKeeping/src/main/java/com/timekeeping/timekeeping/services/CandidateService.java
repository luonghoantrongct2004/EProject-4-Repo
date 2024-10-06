//package com.timekeeping.timekeeping.services;
//
//import com.timekeeping.timekeeping.models.Candidate;
//import com.timekeeping.timekeeping.repositories.CandidateRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class CandidateService {
//
//    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);
//
//    @Autowired
//    private CandidateRepository candidateRepository;
//
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    public List<Candidate> findAll() {
//        return candidateRepository.findAll();
//    }
//
//    public Optional<Candidate> findById(int candidateID) {
//        return candidateRepository.findById(candidateID);
//    }
//
//    public Candidate applyForPosition(Candidate candidate, MultipartFile resume, MultipartFile profilePicture) throws IOException {
//        if (resume != null && !resume.isEmpty()) {
//            String resumePath = saveFile(resume);
//            candidate.setResume(resumePath);  // Save path to Candidate
//        }
//
//        if (profilePicture != null && !profilePicture.isEmpty()) {
//            String profilePicturePath = saveFile(profilePicture);
//            candidate.setProfilePicturePath(profilePicturePath);  // Save path to Candidate
//        }
//
//        return candidateRepository.save(candidate);
//    }
//
//    public Candidate save(Candidate candidate) {
//        return candidateRepository.save(candidate);
//    }
//
//    public void deleteById(int candidateID) {
//        candidateRepository.deleteById(candidateID);
//    }
//
//    public void updateStatus(int candidateID, String status) {
//        Candidate candidate = candidateRepository.findById(candidateID)
//                .orElseThrow(() -> new RuntimeException("Candidate not found"));
//        candidate.setStatus(status);
//        candidateRepository.save(candidate);
//    }
//
//    // Save a file and return the file path
//    public String saveFile(MultipartFile file) throws IOException {
//        // Generate a unique file name or use a specific naming strategy
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        Path filePath = Paths.get(uploadDir, fileName);
//
//        // Ensure the upload directory exists
//        Files.createDirectories(filePath.getParent());
//
//        // Save the file to the specified location
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        // Return the relative path to the file
//        return filePath.toString();
//    }
//}


package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Candidate;
import com.timekeeping.timekeeping.repositories.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, JavaMailSender mailSender) {
        this.candidateRepository = candidateRepository;
        this.mailSender = mailSender;
    }

    // Save a candidate
//    public Candidate save(Candidate candidate) {
//        Candidate savedCandidate = candidateRepository.save(candidate);
//        sendApplicationSuccessEmail(savedCandidate);
//        return savedCandidate;
//    }

//    private void sendApplicationSuccessEmail(Candidate candidate) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(candidate.getEmail()); // Candidate's email address
//            message.setSubject("Application Received Successfully");
//
//            // HTML body for richer formatting
//            String emailContent = buildEmailContent(candidate);
//
//            message.setText(emailContent);  // HTML email
//            mailSender.send(message);
//        } catch (Exception e) {
//            System.err.println("Failed to send email to: " + candidate.getEmail() + ". Error: " + e.getMessage());
//        }
//    }

    // Build a dynamic email content
//    private String buildEmailContent(Candidate candidate) {
//        return "Dear " + candidate.getFirstName() + ",\n\n" +
//                "Thank you for applying for the position. We have received your application successfully. " +
//                "You can track the status of your application on our platform.\n\n" +
//                "Application Details:\n" +
//                "Position: " + candidate.getJobTitle() + "\n" +
//                "Applied on: " + candidate.getApplicationDate() + "\n\n" +
//                "We will contact you soon for the next steps.\n\n" +
//                "Best regards,\n" +
//                "Your Company HR Team";
//    }

    // Find a candidate by ID
    public Optional<Candidate> findById(int candidateID) {
        return candidateRepository.findById(candidateID);
    }

    // Find all candidates
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    // Update candidate status
    public void updateStatus(int candidateID, String status) {
        Candidate candidate = candidateRepository.findById(candidateID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid candidate Id:" + candidateID));
        candidate.setStatus(status);
        candidateRepository.save(candidate);
    }

    // Delete a candidate by ID
    public void deleteById(int candidateID) {
        candidateRepository.deleteById(candidateID);
    }

    private void sendApplicationSuccessEmail(Candidate candidate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(candidate.getEmail()); // Candidate's email address
            message.setSubject("Application Received Successfully");

            String emailContent = buildEmailContent(candidate);
            message.setText(emailContent);  // HTML email
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + candidate.getEmail() + ". Error: " + e.getMessage());
        }
    }

    // Build a dynamic email content
    private String buildEmailContent(Candidate candidate) {
        return "Dear " + candidate.getFirstName() + ",\n\n" +
                "Thank you for applying for the position. We have received your application successfully. " +
                "You can track the status of your application on our platform.\n\n" +
                "Application Details:\n" +
                "Position: " + candidate.getRequirements() + "\n" +
                "Applied on: " + candidate.getCandidateDate() + "\n\n" +
                "We will contact you soon for the next steps.\n\n" +
                "Best regards,\n" +
                "Your Company HR Team";
    }

    public Candidate save(Candidate candidate) {
        // Kiểm tra dữ liệu đầu vào
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null.");
        }
        if (candidate.getEmail() == null || candidate.getRequirements() == null || candidate.getCandidateDate() == null) {
            throw new IllegalArgumentException("Email, Job Title, and Application Date must not be null.");
        }

        try {
            return candidateRepository.save(candidate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save candidate: " + e.getMessage());
        }
    }

}
