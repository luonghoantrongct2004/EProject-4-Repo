package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Candidate;
import com.timekeeping.timekeeping.repositories.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    @Autowired
    private CandidateRepository candidateRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Optional<Candidate> findById(int candidateID) {
        return candidateRepository.findById(candidateID);
    }

    public Candidate save(Candidate candidate, MultipartFile resume, MultipartFile profilePicture) throws IOException {
        if (resume != null && !resume.isEmpty()) {
            String resumePath = saveFile(resume);
            candidate.setResume(resumePath);  // Save path to Candidate
        }

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String profilePicturePath = saveFile(profilePicture);
            candidate.setProfilePicturePath(profilePicturePath);  // Save path to Candidate
        }

        return candidateRepository.save(candidate);
    }

    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public void deleteById(int candidateID) {
        candidateRepository.deleteById(candidateID);
    }

    public void updateStatus(int candidateID, String status) {
        Candidate candidate = candidateRepository.findById(candidateID)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setStatus(status);
        candidateRepository.save(candidate);
    }

    // Save a file and return the file path
    public String saveFile(MultipartFile file) throws IOException {
        // Generate a unique file name or use a specific naming strategy
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Ensure the upload directory exists
        Files.createDirectories(filePath.getParent());

        // Save the file to the specified location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path to the file
        return filePath.toString();
    }
}
