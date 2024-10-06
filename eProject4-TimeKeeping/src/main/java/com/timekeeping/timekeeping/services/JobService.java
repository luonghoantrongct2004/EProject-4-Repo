package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.controllers.JobController;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Job;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Optional<Job> findById(int jobID) {
        return jobRepository.findById(jobID);
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public Optional<Job> findByTitle(String title) {
        return jobRepository.findByTitle(title);
    }
    public void deleteById(int jobID) {
        jobRepository.deleteById(jobID);
    }
//    public Map<String, Long> getJobCountByStatus() {
//        List<Job> jobs = findAll(); // This method should return a list of all jobs
//        return jobs.stream()
//                .collect(Collectors.groupingBy(Job::getStatus, Collectors.counting()));
//    }

    public List<Object[]> countJobsPerMonth() {
        return jobRepository.countJobsPerMonthNative();
    }

//    public Job findById(int jobId) {
//        return jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + jobId));
//    }

    public Job findById(Integer id) {
        return jobRepository.findById(id).orElse(null);
    }
}