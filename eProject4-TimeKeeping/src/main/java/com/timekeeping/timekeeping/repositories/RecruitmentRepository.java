package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer> {
    boolean existsByAccountID(int accountID);

    Recruitment findByAccountID(int accountID);
}