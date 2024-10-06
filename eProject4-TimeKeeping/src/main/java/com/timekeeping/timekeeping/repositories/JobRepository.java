package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {
    Optional<Job> findByTitle(String title);

    @Query(value = "SELECT MONTH(posting_date) AS month, COUNT(*) AS job_count " +
            "FROM Job " +
            "WHERE YEAR(posting_date) = YEAR(CURDATE()) " +
            "GROUP BY MONTH(posting_date)", nativeQuery = true)
    List<Object[]> countJobsPerMonthNative();

}