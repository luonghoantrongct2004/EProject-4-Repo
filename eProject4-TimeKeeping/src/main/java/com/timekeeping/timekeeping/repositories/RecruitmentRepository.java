package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer> {
    boolean existsByAccount(Account account);
    Recruitment findByAccount(Account account);

//    Optional<Recruitment> findByJob_IdAndAccountID(int jobId, int accountId);

//    Optional<Recruitment> findByJobIDAndAccountID(int jobID, int accountID);


    Optional<Recruitment> findByAccountAccountIDAndJobJobID(int accountID, int jobID);

    @Query("SELECT COUNT(r) > 0 FROM Recruitment r WHERE r.account.accountID = :accountId AND r.job.jobID = :jobId")
    boolean existsByAccountIdAndJobId(@Param("accountId") Integer accountId, @Param("jobId") Integer jobId);

//    Optional<Recruitment> findByAccount_IdAndJob_Id(int accountID, int jobID);
}