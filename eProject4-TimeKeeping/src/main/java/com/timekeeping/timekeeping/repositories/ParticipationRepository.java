package com.timekeeping.timekeeping.repositories;

import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.ActivityNotification;
import com.timekeeping.timekeeping.models.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    Optional<Participation> findByAccountAndActivityNotification(Account account, ActivityNotification activityNotification);
    List<Participation> findByAccount_FullNameContaining(String fullName);
    List<Participation> findByActivityNotification_Activity_ActivityNameContaining(String activityName);
    @Query("SELECT p FROM Participation p WHERE p.account.fullName LIKE :fullName AND p.activityNotification.activity.activityName LIKE :activityName")
    List<Participation> findByAccountNameAndActivityName(@Param("fullName") String fullName, @Param("activityName") String activityName);
}
