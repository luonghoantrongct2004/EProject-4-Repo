package com.timekeeping.timekeeping.services;

import com.timekeeping.timekeeping.enums.ParticipationStatus;
import com.timekeeping.timekeeping.models.Account;
import com.timekeeping.timekeeping.models.Activity;
import com.timekeeping.timekeeping.models.ActivityNotification;
import com.timekeeping.timekeeping.models.Participation;
import com.timekeeping.timekeeping.repositories.AccountRepository;
import com.timekeeping.timekeeping.repositories.ActivityNotificationRepository;
import com.timekeeping.timekeeping.repositories.ActivityRepository;
import com.timekeeping.timekeeping.repositories.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ActivityNotificationService activityNotificationService;

    public void confirmParticipation(long id, int accountID, ParticipationStatus status, String reason) {
        Account account = accountRepository.findById(accountID).orElseThrow(() -> new RuntimeException("Account not found"));
        ActivityNotification activityNotification = activityNotificationService.getActivityNotificationById(id).orElseThrow(() -> new RuntimeException("Activity Notification not found"));

        Optional<Participation> participationOpt = participationRepository.findByAccountAndActivityNotification(account, activityNotification);
        Participation participation = participationOpt.orElse(new Participation());
        participation.setAccount(account);
        participation.setActivityNotification(activityNotification);
        participation.setStatus(status);
        if (reason != null && !reason.isEmpty()) {
            participation.setReason(reason);
        }

        participationRepository.save(participation);
    }

    public Participation find(long id, int accountID) {
        Account account = accountRepository.findById(accountID)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        ActivityNotification activityNotification = activityNotificationService.getActivityNotificationById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        return participationRepository.findByAccountAndActivityNotification(account, activityNotification).orElse(null);
    }

    public List<Participation> findAll() {
        return participationRepository.findAll();
    }

    public Optional<Participation> findById(int id) {
        return participationRepository.findById(id);
    }

    public List<Participation> findByAccountName(String fullName) {
        return participationRepository.findByAccount_FullNameContaining(fullName);
    }

    public List<Participation> findByActivityName(String activityName) {
        return participationRepository.findByActivityNotification_Activity_ActivityNameContaining(activityName);
    }

    public List<Participation> findByAccountNameAndActivityName(String fullName, String activityName) {
        return participationRepository.findByAccountNameAndActivityName("%" + fullName + "%", "%" + activityName + "%");
    }


    public Participation save(Participation participation) {
        return participationRepository.save(participation);
    }

    public void deleteById(int id) {
        participationRepository.deleteById(id);
    }
}