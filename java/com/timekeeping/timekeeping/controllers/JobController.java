package com.timekeeping.timekeeping.controllers;

import com.timekeeping.timekeeping.models.Job;
import com.timekeeping.timekeeping.services.JobService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);

    @GetMapping("/list")
    public String listJobs(Model model) {
        List<Job> jobs = jobService.findAll();
        model.addAttribute("jobs", jobs);
        return "jobs/job-list";
    }

    @GetMapping("/list-new")
    public String listNews(Model model) {
        List<Job> jobs = jobService.findAll();
        model.addAttribute("jobs", jobs);
        return "jobs/job-news";
    }

    @GetMapping("/new")
    public String showJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "jobs/job-form";
    }


//    @PostMapping("/save")
//    public String saveJob(@ModelAttribute Job job, @RequestParam("image") MultipartFile file, Model model) {
//        // Check if a job with the same title already exists
//        Optional<Job> existingJobWithTitle = jobService.findByTitle(job.getTitle());
//        if (existingJobWithTitle.isPresent()) {
//            model.addAttribute("error", "A job with this title already exists.");
//            return "jobs/job-form";
//        }
//
//        // Handle file upload
////        if (!file.isEmpty()) {
////            try {
////                String filePath = saveFile(file);
////                job.setPath(filePath);
////            } catch (IOException e) {
////                model.addAttribute("error", "An error occurred while saving the file: " + e.getMessage());
////                return "jobs/job-form";
////            }
////        }
//
//        try {
//            if (!file.isEmpty()) {
//                String filePath = saveFile(file);
//                job.setPath(filePath);
//            }
//        } catch (IOException e) {
//            model.addAttribute("error", "An error occurred while saving the file: " + e.getMessage());
//            return "jobs/job-form";
//        }
//
//        // Save the new job
//        jobService.save(job);
//        return "redirect:/jobs/list";
//    }

//    @PostMapping("/update")
//    public String updateJob(@ModelAttribute Job job, @RequestParam(value = "image", required = false) MultipartFile file, Model model) {
//        try {
//            // Retrieve existing job details
//            Job existingJob = jobService.findById(job.getJobID())
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid job ID: " + job.getJobID()));
//
//            // Check for duplicate job title
////            Optional<Job> existingJobWithTitle = jobService.findByTitle(job.getTitle());
////            if (existingJobWithTitle.isPresent() && existingJobWithTitle.get().getJobID() != existingJob.getJobID()) {
////                model.addAttribute("error", "A job with this title already exists.");
////                return "jobs/job-edit";
////            }
//
//            if (jobService.findByTitle(job.getTitle()).isPresent() &&
//                    jobService.findByTitle(job.getTitle()).get().getJobID() != existingJob.getJobID()) {
//                model.addAttribute("error", "A job with this title already exists.");
//                return "jobs/job-edit";
//            }
//
//            // Handle file upload if a new file is provided
//            if (file != null && !file.isEmpty()) {
//                String filePath = saveFile(file);
//                existingJob.setPath(filePath);
//            }
//            // Handle file upload if a new file is provided
////            if (file != null && !file.isEmpty()) {
////                String filePath = saveFile(file);
////                LOGGER.info("New file path: " + filePath);
////                existingJob.setPath(filePath);
////            } else {
////                LOGGER.info("No new file uploaded, retaining existing path: " + existingJob.getPath());
////            }
//
////            if (file != null && !file.isEmpty()) {
////                String filePath = saveFile(file);
////                LOGGER.info("New file path: " + filePath);
////                existingJob.setPath(filePath);
////            }
//
//            // Update job details
//            existingJob.setTitle(job.getTitle());
//            existingJob.setDescription(job.getDescription());
//            existingJob.setRequirements(job.getRequirements());
//            existingJob.setLocation(job.getLocation());
//            existingJob.setSalaryRange(job.getSalaryRange());
//            existingJob.setPostingDate(job.getPostingDate());
//            existingJob.setStatus(job.getStatus());
//            existingJob.setClosingDate(job.getClosingDate());
//            existingJob.setExperienceYears(job.getExperienceYears());
//
//            // Save the updated job object
//            jobService.save(existingJob);
//            LOGGER.info("Job updated successfully");
//        } catch (IOException e) {
//            LOGGER.error("Error saving file: ", e);
//            model.addAttribute("error", "An error occurred while saving the file: " + e.getMessage());
//            return "jobs/job-edit";
//        } catch (IllegalArgumentException e) {
//            LOGGER.error("Job ID not found: ", e);
//            model.addAttribute("error", "Job not found: " + e.getMessage());
//            return "jobs/job-edit";
//        }
//
//        return "redirect:/jobs/list";
//    }

    @PostMapping("/save")
    public String saveJob(@ModelAttribute Job job, @RequestParam("image") MultipartFile file, Model model) {
        // Check if a job with the same title already exists
        if (jobService.findByTitle(job.getTitle()).isPresent()) {
            model.addAttribute("error", "A job with this title already exists.");
            return "jobs/job-form"; // Return to form on error
        }

        // Handle file upload
        try {
            if (!file.isEmpty()) {
                validateFileType(file); // Validate file type
                String filePath = saveFile(file); // Save the file and get the path
                job.setPath(filePath); // Set the path in the job object
            }
        } catch (IOException e) {
            model.addAttribute("error", "An error occurred while saving the file: " + e.getMessage());
            return "jobs/job-form"; // Return to form on error
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // Handle validation errors
            return "jobs/job-form"; // Return to form on error
        }

        // Save the new job
        jobService.save(job);
        return "redirect:/jobs/list"; // Redirect to job list after saving
    }

    @PostMapping("/update")
    public String updateJob(@ModelAttribute Job job, @RequestParam(value = "image", required = false) MultipartFile file, Model model) {
        try {
            // Retrieve existing job details
            Job existingJob = jobService.findById(job.getJobID())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid job ID: " + job.getJobID()));

            // Check for duplicate job title
            if (jobService.findByTitle(job.getTitle()).isPresent() &&
                    jobService.findByTitle(job.getTitle()).get().getJobID() != existingJob.getJobID()) {
                model.addAttribute("error", "A job with this title already exists.");
                return "jobs/job-edit"; // Return to form on error
            }

            // Handle file upload if a new file is provided
            if (file != null && !file.isEmpty()) {
                validateFileType(file); // Validate file type
                String filePath = saveFile(file); // Save the file and get the path
                existingJob.setPath(filePath); // Update job path with the new file path
            }

            // Update job details
            existingJob.setTitle(job.getTitle());
            existingJob.setDescription(job.getDescription());
            existingJob.setRequirements(job.getRequirements());
            existingJob.setLocation(job.getLocation());
            existingJob.setSalaryRange(job.getSalaryRange());
            existingJob.setPostingDate(job.getPostingDate());
            existingJob.setStatus(job.getStatus());
            existingJob.setClosingDate(job.getClosingDate());
            existingJob.setExperienceYears(job.getExperienceYears());

            // Save the updated job object
            jobService.save(existingJob);
            LOGGER.info("Job updated successfully");
        } catch (IOException e) {
            LOGGER.error("Error saving file: ", e);
            model.addAttribute("error", "An error occurred while saving the file: " + e.getMessage());
            return "jobs/job-edit"; // Return to edit form on error
        } catch (IllegalArgumentException e) {
            LOGGER.error("Job ID not found: ", e);
            model.addAttribute("error", "Job not found: " + e.getMessage());
            return "jobs/job-edit"; // Return to edit form on error
        }

        return "redirect:/jobs/list"; // Redirect to job list after updating
    }

    @GetMapping("/edit/{jobID}")
    public String editJob(@PathVariable int jobID, Model model) {
        Optional<Job> job = jobService.findById(jobID);
        if (job.isPresent()) {
            model.addAttribute("job", job.get());
            return "jobs/job-edit";
        } else {
            model.addAttribute("error", "Job not found");
            return "error";
        }
    }


    @GetMapping("/delete/{jobID}")
    public String deleteJob(@PathVariable int jobID) {
        jobService.deleteById(jobID);
        return "redirect:/jobs/list";
    }


//    private String saveFile(MultipartFile file) throws IOException {
//        String fileName = file.getOriginalFilename();
//        Path filePath = Paths.get(uploadDir).resolve(fileName);
//        Files.createDirectories(filePath.getParent());
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        LOGGER.info("File saved at: " + filePath.toString());
//        return "/images/" + fileName;
//    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("File saved at: " + filePath.toString());
        return "/images/" + fileName; // Return the relative path to the saved image
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Only JPEG and PNG files are allowed.");
        }
    }


    @ControllerAdvice
    public static class GlobalExceptionHandler {

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
    }

    @Component
    public class DirectoryInitializer {

        @Value("${file.upload-dir}")
        private String uploadDir;

        @PostConstruct
        public void init() {
            // Define the directory path from the upload directory property
            File directory = new File(uploadDir);

            // Create directory if it does not exist
            if (!directory.exists()) {
                directory.mkdirs();
                LOGGER.info("Upload directory created at: " + uploadDir);
            }
        }
    }

    @GetMapping("/monthly-count")
    public List<Map<String, Object>> getJobCountPerMonth() {
        List<Object[]> jobCounts = jobService.countJobsPerMonth();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] jobCount : jobCounts) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", jobCount[0]);  // Month number (1-12)
            monthData.put("count", jobCount[1]);  // Job count
            result.add(monthData);
        }

        return result;  // Returns as JSON
    }


//    @GetMapping("/chart")
//    @ResponseBody
//    public List<JobChartData> getJobChartData() {
//        List<Job> jobs = jobService.findAll();
//
//        // Phân tích dữ liệu theo trạng thái
//        Map<String, Long> jobCountByStatus = jobs.stream()
//                .collect(Collectors.groupingBy(Job::getStatus, Collectors.counting()));
//
//        List<JobChartData> chartData = new ArrayList<>();
//        for (Map.Entry<String, Long> entry : jobCountByStatus.entrySet()) {
//            chartData.add(new JobChartData(entry.getKey(), entry.getValue()));
//        }
//
//        return chartData;
//    }

//    public static class JobChartData {
//        private String status;
//        private Long count;
//
//        public JobChartData(String status, Long count) {
//            this.status = status;
//            this.count = count;
//        }
//
//        // Getters và setters
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public Long getCount() {
//            return count;
//        }
//
//        public void setCount(Long count) {
//            this.count = count;
//        }
//    }



    @GetMapping("/chart")
    public String showJobChartPage() {
        return "jobs/chart"; // Points to job-list.html in src/main/resources/templates
    }

//    @GetMapping("/status")
//    @ResponseBody
//    public List<JobChartData> getJobChartData() {
//        Map<String, Long> jobCountByStatus = jobService.getJobCountByStatus();
//
//        List<JobChartData> chartData = new ArrayList<>();
//        for (Map.Entry<String, Long> entry : jobCountByStatus.entrySet()) {
//            chartData.add(new JobChartData(entry.getKey(), entry.getValue()));
//        }
//
//        return chartData;
//    }





}
