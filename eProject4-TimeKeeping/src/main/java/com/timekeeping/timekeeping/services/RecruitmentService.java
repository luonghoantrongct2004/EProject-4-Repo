package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.models.Recruitment;
import com.timekeeping.timekeeping.repositories.RecruitmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    public List<Recruitment> findAll() {
        return recruitmentRepository.findAll();
    }

    public Optional<Recruitment> findById(int recruitmentID) {
        return recruitmentRepository.findById(recruitmentID);
    }

    public boolean existsByAccountID(int accountID) {
        return recruitmentRepository.existsByAccountID(accountID);
    }

    public Recruitment findByAccountID(int accountID) {
        return recruitmentRepository.findByAccountID(accountID);
    }

    public Recruitment save(Recruitment recruitment) {
        return recruitmentRepository.save(recruitment);
    }

    public Recruitment update(int recruitmentID, Recruitment updatedRecruitment) {
        Optional<Recruitment> existingRecruitment = recruitmentRepository.findById(recruitmentID);
        if (existingRecruitment.isPresent()) {
            Recruitment recruitment = existingRecruitment.get();

            // Update fields
            recruitment.setJobID(updatedRecruitment.getJobID());
            recruitment.setStatus(updatedRecruitment.getStatus());
            recruitment.setDescription(updatedRecruitment.getDescription());
            recruitment.setStartDate(updatedRecruitment.getStartDate());
            recruitment.setEndDate(updatedRecruitment.getEndDate());
            recruitment.setInterviewDate(updatedRecruitment.getInterviewDate());
            recruitment.setLocation(updatedRecruitment.getLocation());
            recruitment.setName(updatedRecruitment.getName());
            recruitment.setInterviewType(updatedRecruitment.getInterviewType());
            recruitment.setNotes(updatedRecruitment.getNotes());
            recruitment.setAccountID(updatedRecruitment.getAccountID());

            // Save updated recruitment
            try {
                return recruitmentRepository.save(recruitment);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error saving updated recruitment", e);
            }
        } else {
            throw new RuntimeException("Recruitment not found with id: " + recruitmentID);
        }
    }






    public void deleteById(int recruitmentID) {
        recruitmentRepository.deleteById(recruitmentID);
    }


//    public void updateRecruitment(int recruitmentId, Recruitment updatedRecruitment) {
//        Optional<Recruitment> existingRecruitment = recruitmentRepository.findById(recruitmentId);
//        if (existingRecruitment.isPresent()) {
//            Recruitment recruitment = existingRecruitment.get();
//            recruitment.setRecruitmentId(updatedRecruitment.getRecruitmentId());
//            recruitment.setDescription(updatedRecruitment.getDescription());
//            // Update other fields as necessary
//            recruitmentRepository.save(recruitment);
//        }
//    }
}
