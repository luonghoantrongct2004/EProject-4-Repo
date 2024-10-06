package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Job;
import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.JobRepository;
import com.timekeeping.timekeeping.repositories.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Recruitment> findAll() {
        return recruitmentRepository.findAll();
    }

//    public Optional<Recruitment> findById(int recruitmentID) {
//        return recruitmentRepository.findById(recruitmentID);
//    }

    public boolean existsByAccount(Account account) {
        return recruitmentRepository.existsByAccount(account);
    }

    // Find recruitment by account
    public Recruitment findByAccount(Account account) {
        return recruitmentRepository.findByAccount(account);
    }

    public Recruitment save(Recruitment recruitment) {
        return recruitmentRepository.save(recruitment);
    }

    @Autowired
    public RecruitmentService(RecruitmentRepository recruitmentRepository,
                              JobRepository jobRepository,
                              AccountRepository accountRepository) {
        this.recruitmentRepository = recruitmentRepository;
        this.jobRepository = jobRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
//    public void saveRecruitment(Recruitment recruitment) {
//        // Ensure the account and job are not null
//        if (recruitment.getAccount() == null || recruitment.getJob() == null) {
//            throw new IllegalArgumentException("Account and Job cannot be null");
//        }
//
//        // Save recruitment
//        recruitmentRepository.save(recruitment);
//    }
    public Recruitment findById(int id) {
        return recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruitment not found: " + id));
    }

    @Transactional
    public void saveRecruitment(Recruitment recruitment) {
        if (recruitment.getJob() != null && recruitment.getAccount() != null) {
            recruitmentRepository.save(recruitment);
        } else {
            throw new IllegalArgumentException("Job and Account must not be null");
        }
    }

    public Optional<Recruitment> findByID(int recruitmentID) {
        return recruitmentRepository.findById(recruitmentID);
    }


    public boolean isJobAndAccountUnique(int jobID, int accountID) {
        return recruitmentRepository.findByAccountAccountIDAndJobJobID(jobID, accountID).isEmpty();
    }

//    public boolean existsByAccountIdAndJobId(int accountId, int jobId) {
//        return recruitmentRepository.existsByAccountIdAndJobId(accountId, jobId);
//    }

//    public boolean existsByAccountAndJob(int accountID, int jobID) {
//        return recruitmentRepository.existsByAccount_AccountIDAndJob_JobID(accountID, jobID);
//    }


    public boolean existsByAccountIdAndJobId(int accountId, int jobId) {
        return recruitmentRepository.existsByAccountIdAndJobId(accountId, jobId);
    }


    public Recruitment update(int recruitmentID, Recruitment updatedRecruitment) {
        Optional<Recruitment> existingRecruitmentOpt = recruitmentRepository.findById(recruitmentID);
        if (existingRecruitmentOpt.isPresent()) {
            Recruitment existingRecruitment = existingRecruitmentOpt.get();

            // Update fields from the updated recruitment
            existingRecruitment.setJob(updatedRecruitment.getJob()); // Assuming Job is an entity
            existingRecruitment.setStatus(updatedRecruitment.getStatus());
            existingRecruitment.setDescription(updatedRecruitment.getDescription());
            existingRecruitment.setStartDate(updatedRecruitment.getStartDate());
            existingRecruitment.setEndDate(updatedRecruitment.getEndDate());
            existingRecruitment.setInterviewDate(updatedRecruitment.getInterviewDate());
            existingRecruitment.setLocation(updatedRecruitment.getLocation());
            existingRecruitment.setName(updatedRecruitment.getName());
            existingRecruitment.setInterviewType(updatedRecruitment.getInterviewType());
            existingRecruitment.setNotes(updatedRecruitment.getNotes());
            existingRecruitment.setAccount(updatedRecruitment.getAccount()); // Assuming Account is an entity

            // Save the updated recruitment
            return recruitmentRepository.save(existingRecruitment);
        } else {
            throw new RuntimeException("Recruitment not found with id: " + recruitmentID);
        }
    }



    public void deleteById(int recruitmentID) {
        recruitmentRepository.deleteById(recruitmentID);
    }


    public void updateRecruitment(int recruitmentId, Recruitment updatedRecruitment) {
        Optional<Recruitment> existingRecruitmentOpt = recruitmentRepository.findById(recruitmentId);
        if (existingRecruitmentOpt.isPresent()) {
            Recruitment existingRecruitment = existingRecruitmentOpt.get();
            // Update specific fields
            existingRecruitment.setDescription(updatedRecruitment.getDescription());
            // Add other field updates as needed
            recruitmentRepository.save(existingRecruitment);
        } else {
            throw new RuntimeException("Recruitment not found with id: " + recruitmentId);
        }
    }
}
